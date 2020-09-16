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
package com.thirtyai.nezha.web.security.jwt.host;

import javax.servlet.http.HttpServletRequest;

/**
 * jwt token host interface
 * Default host is header , will provide
 * RequestBodyTokenHost {@link RequestBodyTokenHost}
 * <p>
 * json format:
 * {
 * ....
 * token: token value,
 * ....
 * }
 * </p>
 * CookieTokenHost {@link CookieTokenHost}
 * ParameterCookieHost {@link ParameterTokenHost}
 *
 * @author kyleju
 */
public interface IJwtTokenHost {
	/**
	 * host access token
	 *
	 * @param accessToken
	 */
	default void host(String accessToken, HttpServletRequest httpServletRequest) {
	}

	/**
	 * get access token
	 *
	 * @param httpServletRequest http servlet request
	 * @return
	 */
	String getAccessToken(HttpServletRequest httpServletRequest);

	/**
	 * get order
	 *
	 * @return
	 */
	int getOrder();
}
