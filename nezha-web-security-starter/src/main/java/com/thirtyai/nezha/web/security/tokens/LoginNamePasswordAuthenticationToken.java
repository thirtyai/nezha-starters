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
package com.thirtyai.nezha.web.security.tokens;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * login name password authentication token
 *
 * @author kyleju
 */
@Getter
public class LoginNamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private String loginName;
	private String password;

	public LoginNamePasswordAuthenticationToken(String loginName, String password) {
		super(loginName, password);
		this.loginName = loginName;
		this.password = password;
	}

	public LoginNamePasswordAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

	public LoginNamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
}
