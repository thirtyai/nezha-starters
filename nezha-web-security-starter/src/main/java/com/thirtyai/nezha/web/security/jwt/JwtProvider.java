/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.web.security.jwt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.i18n.Status;
import com.thirtyai.nezha.web.security.exception.I18nSecurityAuthenticationException;
import com.thirtyai.nezha.web.security.jwt.host.IJwtTokenHost;
import com.thirtyai.nezha.web.security.props.NezhaSecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * jwt provider
 *
 * @author kyleju
 */
@Data
@Slf4j
@SuppressWarnings("unchecked")
public class JwtProvider implements InitializingBean {

	private static String AUTHORITIES_KEY = "a";
	private static String AUTHORITIES_REFRESH_KEY = "a_r";
	private static String DISTRIBUTION_STATUS_CODE_KEY = "d_s_c_k";
	private NezhaSecurityProperties securityProperties;
	private Key key;
	private long tokenValidityInMillisecondsForRememberMe;
	private long tokenValidityInMilliseconds;
	private long refreshTokenValidityInSeconds;
	private List<IJwtTokenHost> jwtTokenHosts;
	private IJwtTokenSafeExtender jwtTokenExtenderInterface = new DefaultJwtTokenSafeExtender();
	private IJwtAuthProvider jwtAuthProvider;

	public JwtProvider(NezhaSecurityProperties nezhaSecurityProperties, ObjectProvider<List<IJwtTokenHost>> jwtTokenHosts, ObjectProvider<IJwtTokenSafeExtender> jwtTokenExtendInterfaceObjectProvider, ObjectProvider<IJwtAuthProvider> jwtAuthProviderObjectProvider) {
		securityProperties = nezhaSecurityProperties;
		this.jwtTokenHosts = jwtTokenHosts.getIfAvailable();
		jwtTokenExtendInterfaceObjectProvider.ifAvailable(item -> jwtTokenExtenderInterface = item);
		jwtAuthProviderObjectProvider.ifAvailable(item -> jwtAuthProvider = item);
		if (CollUtil.isNotEmpty(this.jwtTokenHosts)) {
			this.jwtTokenHosts.sort(Comparator.comparingInt(IJwtTokenHost::getOrder));
		}
	}

	@Override
	public void afterPropertiesSet() {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.securityProperties.getJwt().getBase64Secret()));
		this.tokenValidityInMilliseconds = this.securityProperties.getJwt().getTokenValidityInSeconds() * 1000;
		this.tokenValidityInMillisecondsForRememberMe = this.securityProperties.getJwt().getTokenValidityInSecondsForRememberMe() * 1000;
		this.refreshTokenValidityInSeconds = this.securityProperties.getJwt().getRefreshTokenValidityInSeconds() * 1000;
	}

	/**
	 * create jwt token
	 *
	 * @param authentication authentication
	 * @param rememberMe     remember me
	 * @return JwtToken {@link JwtToken}
	 */
	public JwtToken createJwtToken(HttpServletResponse httpServletResponse, Authentication authentication, boolean rememberMe) {
		JwtToken jwtToken = new JwtToken();
		String subjectValue = this.jwtTokenExtenderInterface.getSubject(authentication.getPrincipal());
		jwtToken.setAccessToken(createAccessToken(authentication, subjectValue, rememberMe));
		jwtToken.setRefreshToken(createRefreshToken(authentication, subjectValue));
		if (httpServletResponse != null) {
			httpServletResponse.setHeader(this.securityProperties.getHeaderTokenKey(), "Bearer " + jwtToken.getAccessToken());
		}
		return jwtToken;
	}

	/**
	 * create jwt token
	 *
	 * @param refreshToken refresh token
	 * @return JwtToken {@link JwtToken}
	 */
	public JwtToken refreshJwtToken(HttpServletResponse httpServletResponse, String refreshToken, boolean rememberMe) throws I18nSecurityAuthenticationException {
		Authentication authentication = getAuthenticationFromRefresh(refreshToken);
		return createJwtToken(httpServletResponse, authentication, rememberMe);
	}

	/**
	 * get access token
	 *
	 * @param httpServletRequest http servlet request
	 * @return access token
	 */
	public String getAccessToken(HttpServletRequest httpServletRequest) {
		String jwt = resolveToken(httpServletRequest);
		if (StrUtil.isBlank(jwt) && jwtTokenHosts != null) {
			for (IJwtTokenHost item : jwtTokenHosts) {
				jwt = item.getAccessToken(httpServletRequest);
				if (StrUtil.isNotBlank(jwt)) {
					break;
				}
			}
		}
		return jwt;
	}

	/**
	 * resolve token
	 *
	 * @param request request
	 * @return token
	 */
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(securityProperties.getHeaderTokenKey());
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	/**
	 * get claims
	 *
	 * @param accessToken accessToken
	 * @return value
	 */
	public Claims getClaims(String accessToken) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(decode(accessToken)).getBody();
	}

	/**
	 * get authentication
	 *
	 * @param accessToken access token
	 * @return Authentication {@link Authentication}
	 */
	public Authentication getAuthentication(String accessToken) {
		if (jwtAuthProvider != null) {
			return jwtAuthProvider.getAuthentication(accessToken);
		}
		if (!validateToken(accessToken, TokenType.Access)) {
			return null;
		}
		Claims claims = getClaims(accessToken);
		String claimsAuthorityValue = claims.get(AUTHORITIES_KEY).toString();
		// through jwt token extender get authorities
		JwtUser jwtUser = jwtTokenExtenderInterface.getJwtUser(claims.getSubject(), claimsAuthorityValue);
		if (jwtUser == null) {
			return null;
		}
		return new UsernamePasswordAuthenticationToken(jwtUser, "", jwtUser.getAuthorities());
	}

	/**
	 * get authentication from refresh
	 *
	 * @param refreshToken refresh token
	 * @return Authentication {@link Authentication}
	 */
	public Authentication getAuthenticationFromRefresh(String refreshToken) {
		if (!validateToken(refreshToken, TokenType.Refresh)) {
			return null;
		}
		Claims claims = getClaims(refreshToken);

		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_REFRESH_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).collect(toList());

		User principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	/**
	 * create access token
	 *
	 * @param authentication authentication {@link Authentication}
	 * @param rememberMe     remember me
	 * @return value
	 */
	private String createAccessToken(Authentication authentication, String subjectValue, boolean rememberMe) {
		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity;
		if (rememberMe) {
			validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
		} else {
			validity = new Date(now + this.tokenValidityInMilliseconds);
		}

		if (StrUtil.isEmpty(subjectValue)) {
			throw new I18nSecurityAuthenticationException(Status.Security_Jwt_Token_Subject_Is_Null);
		}
		JwtBuilder jwtBuilder = Jwts.builder().setSubject(subjectValue).claim(AUTHORITIES_KEY, authorities).signWith(key, SignatureAlgorithm.HS512).setExpiration(validity);
		// add distribution status code, if the code is not null.
		String distributionStatusCode = this.jwtTokenExtenderInterface.getTokenDistributionStatusCode(subjectValue);
		if (distributionStatusCode != null) {
			jwtBuilder.claim(DISTRIBUTION_STATUS_CODE_KEY, distributionStatusCode);
		}
		return encode(jwtBuilder.compact());
	}

	/**
	 * create refresh token
	 *
	 * @param authentication authentication {@link Authentication}
	 * @return value
	 */
	private String createRefreshToken(Authentication authentication, String subjectValue) {
		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

		long now = new Date().getTime();
		Date validity = new Date(now + this.refreshTokenValidityInSeconds);

		if (StrUtil.isEmpty(subjectValue)) {
			throw new I18nSecurityAuthenticationException(Status.Security_Jwt_Token_Subject_Is_Null);
		}
		JwtBuilder jwtBuilder = Jwts.builder().setSubject(subjectValue).claim(AUTHORITIES_REFRESH_KEY, authorities).signWith(key, SignatureAlgorithm.HS512).setExpiration(validity);

		// add distribution status code, if the code is not null.
		String distributionStatusCode = this.jwtTokenExtenderInterface.getTokenDistributionStatusCode(subjectValue);
		if (distributionStatusCode != null) {
			jwtBuilder.claim(DISTRIBUTION_STATUS_CODE_KEY, distributionStatusCode);
		}

		return encode(jwtBuilder.compact());
	}

	/**
	 * validate token
	 *
	 * @param authToken access token & refresh token
	 * @return boolean
	 */
	public boolean validateToken(String authToken, TokenType tokenType) throws I18nSecurityAuthenticationException {
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(decode(authToken));
			if (claimsJws.getBody().get(DISTRIBUTION_STATUS_CODE_KEY) != null) {
				if (claimsJws.getBody().get(DISTRIBUTION_STATUS_CODE_KEY).equals(this.jwtTokenExtenderInterface.getTokenDistributionStatusCode(claimsJws.getBody().getSubject()))) {
					return true;
				} else {
					throw new I18nSecurityAuthenticationException(Status.Security_Access_Token_Expired);
				}
			} else {
				String code = this.jwtTokenExtenderInterface.getTokenDistributionStatusCode(claimsJws.getBody().getSubject());
				if (code != null) {
					throw new I18nSecurityAuthenticationException(Status.Security_Access_Token_Expired);
				}
			}
			return true;
		} catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ignored) {
			throw new I18nSecurityAuthenticationException(Status.Security_No_Reason_Error);
		} catch (ExpiredJwtException e) {
			if (tokenType == TokenType.Refresh) {
				throw new I18nSecurityAuthenticationException(Status.Security_Refresh_Token_Expired);
			}
			throw new I18nSecurityAuthenticationException(Status.Security_Access_Token_Expired);
		}
	}

	private String encode(String rawCode) {
		try {
			return this.jwtTokenExtenderInterface.encode(rawCode);
		} catch (Exception ex) {
			log.error(ExceptionUtil.getMessage(ex));
			throw new I18nSecurityAuthenticationException(Status.Security_Jwt_Token_Encode_Error);
		}
	}

	private String decode(String code) {
		try {
			return this.jwtTokenExtenderInterface.decode(code);
		} catch (Exception ex) {
			log.error(ExceptionUtil.getMessage(ex));
			throw new I18nSecurityAuthenticationException(Status.Security_Jwt_Token_Decode_Error);
		}
	}

	/**
	 * clean
	 */
	public void clean() {
		if (jwtAuthProvider != null) {
			jwtAuthProvider.clean();
		} else {
			this.jwtTokenExtenderInterface.clean();
		}
	}

	@RequiredArgsConstructor
	public static class DefaultJwtTokenSafeExtender implements IJwtTokenSafeExtender {

		@Override
		public String getTokenDistributionStatusCode(String subjectValue) {
			return null;
		}

		@Override
		public JwtUser getJwtUser(String subjectValue, String claimsAuthorityValue) {
			return new JwtUser(subjectValue, subjectValue, "", getAuthorities(claimsAuthorityValue), null);
		}
	}

	public enum TokenType {
		Access, Refresh
	}
}
