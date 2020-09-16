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
package com.thirtyai.nezha.web.security.provider;

import com.thirtyai.nezha.i18n.Status;
import com.thirtyai.nezha.web.security.exception.NoBadCredentialsSecurityAuthenticationException;
import com.thirtyai.nezha.web.security.exception.NoProcessorSecurityAuthenticationException;
import com.thirtyai.nezha.web.security.exception.I18nSecurityAuthenticationException;
import com.thirtyai.nezha.web.security.processor.IAuthenticationTokenProcessor;
import com.thirtyai.nezha.web.security.service.MultipleTokenAuthenticationUserDetailsService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Multiple token authentication provider
 *
 * @author kyleju
 */
public class MultipleTokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Getter
	private Map<Class<? extends UsernamePasswordAuthenticationToken>, IAuthenticationTokenProcessor> tokenProcessorMap;

	@Setter
	@Getter
	private MultipleTokenAuthenticationUserDetailsService tokenAuthenticationUserDetailsService;

	/**
	 * get Token authentication Processor
	 *
	 * @param type authentication token processor corresponding authentication token
	 * @return token processor {@link IAuthenticationTokenProcessor}
	 */
	public IAuthenticationTokenProcessor getAuthenticationTokenProcessor(Class<? extends UsernamePasswordAuthenticationToken> type) {
		if (tokenProcessorMap != null) {
			return tokenProcessorMap.get(type);
		}
		return null;
	}

	public MultipleTokenAuthenticationProvider(List<IAuthenticationTokenProcessor> authenticationTokenProcessors, MultipleTokenAuthenticationUserDetailsService multipleTokenAuthenticationUserDetailsService) {
		super();
		Assert.notEmpty(authenticationTokenProcessors, "token processor not empty.");
		if (tokenProcessorMap == null) {
			tokenProcessorMap = new HashMap<>();
		}
		authenticationTokenProcessors.stream().forEach(item -> {
			item.supports().forEach(c -> {
				if (!tokenProcessorMap.containsKey(c)) {
					tokenProcessorMap.put(c, item);
				}
			});
		});
		this.tokenAuthenticationUserDetailsService = multipleTokenAuthenticationUserDetailsService;
	}

	/**
	 * Provider 验证认证 主要逻辑
	 *
	 * @param userDetails    user details
	 * @param authentication authentication
	 * @throws I18nSecurityAuthenticationException
	 */
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws I18nSecurityAuthenticationException {
		if (authentication == null || authentication.getCredentials() == null) {
			throw new NoBadCredentialsSecurityAuthenticationException();
		}
		IAuthenticationTokenProcessor iAuthenticationTokenProcessor = getAuthenticationTokenProcessor(authentication.getClass());
		if (iAuthenticationTokenProcessor == null) {
			throw new NoProcessorSecurityAuthenticationException();
		}
		iAuthenticationTokenProcessor.authentication(authentication, userDetails);
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		try {
			UserDetails loadedUser = this.tokenAuthenticationUserDetailsService.loadUserByUsernameAndToken(username, authentication.getClass());
			if (loadedUser == null) {
				throw new I18nSecurityAuthenticationException(Status.Security_User_Not_Exist);
			}
			return loadedUser;
		} catch (UsernameNotFoundException ex) {
			throw new I18nSecurityAuthenticationException(Status.Security_User_Not_Exist);
		} catch (Exception ex) {
			throw new I18nSecurityAuthenticationException(Status.Security_No_Reason_Error);
		}
	}


	/**
	 * provider distribute credential
	 *
	 * @param token token
	 * @return value
	 */
	public boolean distributeCredential(UsernamePasswordAuthenticationToken token) {
		IAuthenticationTokenProcessor iAuthenticationTokenProcessor = getAuthenticationTokenProcessor(token.getClass());

		if (iAuthenticationTokenProcessor != null) {
			return iAuthenticationTokenProcessor.distributeCredential(token);
		}
		return false;
	}
}
