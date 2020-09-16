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
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.core.web.filter.AbstractNezhaWebFilter;
import com.thirtyai.nezha.common.wrap.Resp;
import com.thirtyai.nezha.common.wrap.RespBuilder;
import com.thirtyai.nezha.common.util.JsonUtil;
import com.thirtyai.nezha.web.security.exception.I18nSecurityAuthenticationException;
import com.thirtyai.nezha.web.security.props.NezhaSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * jwt filter
 *
 * @author kyleju
 */
@Slf4j
public class JwtFilterNezha extends AbstractNezhaWebFilter {
	private JwtProvider provider;
	private NezhaSecurityProperties securityProperties;

	public JwtFilterNezha(JwtProvider jwtProvider, NezhaSecurityProperties nezhaSecurityProperties) {
		super(CollUtil.addAllIfNotContains(CollUtil.newArrayList(nezhaSecurityProperties.getAuthUrl()), nezhaSecurityProperties.getUrlExclusions()));
		this.provider = jwtProvider;
		this.securityProperties = nezhaSecurityProperties;
		Assert.notNull(this.provider, "iJwtAuthProvider is null");
		Assert.notNull(this.securityProperties, "security properties is null");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		if (!isIgnore(httpServletRequest.getServletPath())) {
			try {
				String jwt = provider.getAccessToken(httpServletRequest);
				String requestURI = httpServletRequest.getRequestURI();

				if (StrUtil.isNotBlank(jwt)) {
					Authentication authentication = provider.getAuthentication(jwt);
					if (authentication != null) {
						SecurityContextHolder.getContext().setAuthentication(authentication);
						log.debug("set authentication to security context for '{}', uri: {}", authentication.getName(), requestURI);
					} else {
						log.debug("no valid jwt token found, uri: {}", requestURI);
					}
				} else {
					log.debug("no valid jwt token found, uri: {}", requestURI);
				}
			} catch (I18nSecurityAuthenticationException ex) {
				if (httpServletRequest.getContentType() == null || httpServletRequest.getContentType().equals(MediaType.TEXT_HTML_VALUE)) {
					httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getI18n().getDesc(httpServletRequest.getLocale().toString()));
				} else {
					Resp<String> resp = RespBuilder.build(HttpStatus.OK.value(), ex.getI18n(), StrUtil.EMPTY, httpServletRequest.getLocale().toString());
					httpServletResponse.getWriter().write(Objects.requireNonNull(JsonUtil.toJson(resp)));
				}
				return;
			} finally {
				provider.clean();
			}
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
