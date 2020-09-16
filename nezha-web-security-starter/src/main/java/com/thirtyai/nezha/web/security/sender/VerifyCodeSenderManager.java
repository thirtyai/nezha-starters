/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.web.security.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kyleju
 */
public abstract class VerifyCodeSenderManager implements InitializingBean {
	@Setter
	private IVerifyCodeSender currentVerifyCodeSender;

	@Getter
	private List<IVerifyCodeSender> verifyCodeSenders;

	public void addVeritySender(IVerifyCodeSender iVerifyCodeSender) {
		if (verifyCodeSenders == null) {
			verifyCodeSenders = new ArrayList<>();
		}
		verifyCodeSenders.add(iVerifyCodeSender);
	}

	@Override
	public void afterPropertiesSet() {
		chooseVerifySender();
	}

	public IVerifyCodeSender getCurrentVerifyCodeSender() {
		return currentVerifyCodeSender;
	}

	/**
	 * 选择一个最基本的VerifySender
	 */
	public abstract void chooseVerifySender();
}
