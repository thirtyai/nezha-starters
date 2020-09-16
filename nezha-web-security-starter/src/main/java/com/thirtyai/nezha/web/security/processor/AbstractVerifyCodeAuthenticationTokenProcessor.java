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
package com.thirtyai.nezha.web.security.processor;

import com.thirtyai.nezha.web.security.sender.VerifyCodeSenderManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * abstract verify code authentication token processor
 *
 * @author kyleju
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractVerifyCodeAuthenticationTokenProcessor implements IAuthenticationTokenProcessor {

	@NonNull
	private VerifyCodeSenderManager verifyCodeSenderManager;

	public boolean sendVerifyCode(String receiver, String code) {
		if (verifyCodeSenderManager.getCurrentVerifyCodeSender() != null) {
			return verifyCodeSenderManager.getCurrentVerifyCodeSender().send(receiver, code);
		} else {
			log.warn("verify sender is null, send code fail.");
		}
		return false;
	}

	@Override
	public boolean distributeCredential(UsernamePasswordAuthenticationToken userDetail) {
		return sendVerifyCode(userDetail.getName(), userDetail.getCredentials().toString());
	}

}
