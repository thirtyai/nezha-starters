/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.web.security.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.SecureUtil;
import com.thirtyai.nezha.i18n.Status;
import com.thirtyai.nezha.common.lang.RegexPool;
import com.thirtyai.nezha.web.security.exception.I18nSecurityAuthenticationException;
import com.thirtyai.nezha.web.security.tokens.LoginNamePasswordAuthenticationToken;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * login name password token processor
 * authentication token must only one Processor.
 *
 * @author kyleju
 * @See MultipleTokenAuthenticationProvider#constructor(List < IAuthenticationTokenProcessor >, MultipleTokenAuthenticationUserDetailsService)
 */
public class LoginNamePasswordAuthenticationTokenProcessor implements IAuthenticationTokenProcessor {

	private ProcessorAware processorAware;

	@Autowired
	public void setProcessorAware(ObjectProvider<ProcessorAware> processorAwareObjectProvider) {
		this.processorAware = processorAwareObjectProvider.getIfAvailable();
	}

	@Override
	public void authentication(UsernamePasswordAuthenticationToken authentication, UserDetails userDetails) {
		if (authentication instanceof LoginNamePasswordAuthenticationToken) {
			LoginNamePasswordAuthenticationToken authenticationToken = (LoginNamePasswordAuthenticationToken) authentication;
			if (userDetails == null || !SecureUtil.md5(authenticationToken.getPassword()).toLowerCase().equals(userDetails.getPassword().toLowerCase())) {
				if (ReUtil.isMatch(RegexPool.EMAIL, authenticationToken.getLoginName())) {
					throw new I18nSecurityAuthenticationException(Status.Security_Email_Password_Error);
				} else {
					throw new I18nSecurityAuthenticationException(Status.Security_LoginName_Password_Error);
				}
			}
			if (this.processorAware != null) {
				this.processorAware.loginNamePasswordAuthentication(authenticationToken, userDetails);
			}
		} else {
			throw new I18nSecurityAuthenticationException(Status.Security_No_Reason_Error);
		}
	}

	@Override
	public List<Class<? extends UsernamePasswordAuthenticationToken>> supports() {
		return CollUtil.newArrayList(LoginNamePasswordAuthenticationToken.class);
	}
}
