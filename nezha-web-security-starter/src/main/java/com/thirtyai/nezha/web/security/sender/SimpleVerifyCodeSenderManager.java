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

/**
 * simple verify code sender manager, the choose logic is only select first one set the current verify sender.
 * @author kyleju
 */
public class SimpleVerifyCodeSenderManager extends VerifyCodeSenderManager {
	@Override
	public void chooseVerifySender() {
		if (this.getVerifyCodeSenders() != null && this.getVerifyCodeSenders().size() > 0) {
			this.setCurrentVerifyCodeSender(this.getVerifyCodeSenders().get(0));
		}
	}
}
