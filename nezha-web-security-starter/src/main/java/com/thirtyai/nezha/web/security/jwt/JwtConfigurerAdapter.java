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

import com.thirtyai.nezha.web.security.filter.MultipleTokenAuthenticationFilter;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * jwt configurer adapter
 * add JwtFilter {@link JwtFilterNezha} to FilterChain
 *
 * @author kyleju
 */
public class JwtConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private JwtFilterNezha jwtFilter;

	public JwtConfigurerAdapter(JwtFilterNezha jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Override
	public void configure(HttpSecurity http) {
		http.addFilterBefore(this.jwtFilter, MultipleTokenAuthenticationFilter.class);
	}
}
