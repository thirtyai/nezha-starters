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
package com.thirtyai.nezha.web.security.config;

import cn.hutool.core.collection.CollUtil;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.web.security.filter.MultipleTokenAuthenticationFilter;
import com.thirtyai.nezha.web.security.handler.JwtAccessDeniedHandler;
import com.thirtyai.nezha.web.security.handler.JwtAuthenticationEntryPoint;
import com.thirtyai.nezha.web.security.jwt.JwtConfigurerAdapter;
import com.thirtyai.nezha.web.security.props.NezhaSecurityProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * @author kyleju
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SpringWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private String[] defaultExclusions = NezhaConstant.DEFAULT_STATIC_EXCLUSIONS;

	@NonNull
	private NezhaSecurityProperties nezhaSecurityProperties;
	@NonNull
	private ObjectProvider<CorsFilter> corsFilterObjectProvider;
	@NonNull
	private JwtAuthenticationEntryPoint authenticationErrorHandler;
	@NonNull
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;
	@NonNull
	private JwtConfigurerAdapter jwtConfigurerAdapter;

	@Override
	public void configure(WebSecurity web) {

		// enable option method (normally before ajax request, the option method request will be sent)
		if (nezhaSecurityProperties.isOptionMethod()) {
			web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
		}

		// allow anonymous resource requests
		if (nezhaSecurityProperties.getStaticExclusions() != null) {
			List<String> staticExclusions = getExclusions(nezhaSecurityProperties.getStaticExclusions(), defaultExclusions);
			web.ignoring().antMatchers(staticExclusions.toArray(new String[staticExclusions.size()]));
		}
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		List<String> urlExclusions = getExclusions(this.nezhaSecurityProperties.getUrlExclusions(), new String[]{this.nezhaSecurityProperties.getAuthUrl()});
		httpSecurity.csrf().disable();
		if (corsFilterObjectProvider.getIfAvailable() != null) {
			httpSecurity.addFilterBefore(corsFilterObjectProvider.getIfAvailable(), UsernamePasswordAuthenticationFilter.class);
		}

		httpSecurity.addFilterAt(new MultipleTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		httpSecurity.exceptionHandling()
			.authenticationEntryPoint(authenticationErrorHandler)
			.accessDeniedHandler(jwtAccessDeniedHandler)

			// enable same origin
			.and()
			.headers()
			.frameOptions()
			.sameOrigin()

			// create no session
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.authorizeRequests()
			.antMatchers(urlExclusions.toArray(new String[urlExclusions.size()])).permitAll()
			.anyRequest().authenticated()

			.and()
			.apply(jwtConfigurerAdapter);
	}

	private List<String> getExclusions(List<String> configExclusions, String[] strings) {
		if (strings != null && strings.length > 0) {
			return CollUtil.addAllIfNotContains(configExclusions, Arrays.asList(strings));
		}
		return configExclusions;
	}
}
