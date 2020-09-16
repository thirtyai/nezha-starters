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
package com.thirtyai.nezha.web.security.exception;

import com.thirtyai.nezha.common.exception.II18nException;
import com.thirtyai.nezha.common.i18n.I18n;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;

/**
 * i18n Authentication Exception
 *
 * @author kyleju
 */
public class I18nSecurityAuthenticationException extends AuthenticationException implements II18nException {
	private I18n i18n;

	public I18nSecurityAuthenticationException(I18n i18n) {
		super(i18n.getDesc(LocaleContextHolder.getLocale().toString()));
		this.i18n = i18n;
	}

	public I18nSecurityAuthenticationException(I18n i18n, Throwable t) {
		super(i18n.getDesc(LocaleContextHolder.getLocale().toString()), t);
		this.i18n = i18n;
	}

	public I18nSecurityAuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}

	public I18nSecurityAuthenticationException(String msg) {
		super(msg);
	}

	@Override
	public I18n getI18n() {
		return this.i18n;
	}
}
