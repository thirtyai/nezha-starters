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

import com.thirtyai.nezha.web.security.handler.*;
import com.thirtyai.nezha.web.security.jwt.*;
import com.thirtyai.nezha.web.security.jwt.host.IJwtTokenHost;
import com.thirtyai.nezha.web.security.jwt.host.ParameterTokenHost;
import com.thirtyai.nezha.web.security.jwt.host.RequestBodyTokenHost;
import com.thirtyai.nezha.web.security.processor.IAuthenticationTokenProcessor;
import com.thirtyai.nezha.web.security.processor.LoginNamePasswordAuthenticationTokenProcessor;
import com.thirtyai.nezha.web.security.props.NezhaSecurityProperties;
import com.thirtyai.nezha.web.security.provider.MultipleTokenAuthenticationProvider;
import com.thirtyai.nezha.web.security.service.MultipleTokenAuthenticationUserDetailsService;
import com.thirtyai.nezha.web.security.service.MultipleTokenUserDetailsChecker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.List;

/**
 * nezha security configuration
 *
 * @author kyleju
 */
@Configuration
@EnableConfigurationProperties(NezhaSecurityProperties.class)
public class SecurityConfiguration {

	private NezhaSecurityProperties securityProperties;

	public SecurityConfiguration(NezhaSecurityProperties nezhaSecurityProperties) {
		this.securityProperties = nezhaSecurityProperties;
	}

	@Bean
	public IAuthenticationTokenProcessor loginNamePasswordAuthenticationTokenProcessor() {
		return new LoginNamePasswordAuthenticationTokenProcessor();
	}

	@Bean
	@ConditionalOnBean({IAuthenticationTokenProcessor.class, MultipleTokenAuthenticationUserDetailsService.class})
	public MultipleTokenAuthenticationProvider nezhaMultipleTokenAuthenticationProvider(ObjectProvider<List<IAuthenticationTokenProcessor>> authenticationTokenProcessors
		, ObjectProvider<MultipleTokenAuthenticationUserDetailsService> multipleTokenAuthenticationUserDetailsServices) {
		MultipleTokenAuthenticationProvider multipleTokenAuthenticationProvider = new MultipleTokenAuthenticationProvider(authenticationTokenProcessors.getIfAvailable(), multipleTokenAuthenticationUserDetailsServices.getIfAvailable());
		multipleTokenAuthenticationProvider.setPreAuthenticationChecks(new MultipleTokenUserDetailsChecker());
		return multipleTokenAuthenticationProvider;
	}

	@Bean
	@ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new JwtAuthenticationSuccessHandler();
	}

	@Bean
	@ConditionalOnMissingBean(AuthenticationFailureHandler.class)
	public AuthenticationFailureHandler jwtAuthenticationFailureHandler() {
		return new JwtAuthenticationFailureHandler();
	}

	@Bean
	public JwtProvider jwtProvider(ObjectProvider<List<IJwtTokenHost>> tokenHostObjectProvider, ObjectProvider<IJwtTokenSafeExtender> jwtTokenExtenderInterfaceObjectProvider, ObjectProvider<IJwtAuthProvider> jwtAuthProviderObjectProvider) {
		return new JwtProvider(securityProperties, tokenHostObjectProvider, jwtTokenExtenderInterfaceObjectProvider, jwtAuthProviderObjectProvider);
	}

	@Bean
	public JwtFilterNezha jwtFilter(JwtProvider jwtProvider) {
		return new JwtFilterNezha(jwtProvider, securityProperties);
	}

	@Bean
	public JwtConfigurerAdapter jwtConfigurerAdapter(JwtFilterNezha jwtFilter) {
		return new JwtConfigurerAdapter(jwtFilter);
	}

	@Bean
	public RequestBodyTokenHost requestBodyTokenHost() {
		return new RequestBodyTokenHost();
	}

	@Bean
	@ConditionalOnProperty(prefix = "nezha.security", value = "paramToken", havingValue = "true")
	public ParameterTokenHost parameterTokenHost() {
		return new ParameterTokenHost();
	}

	@Bean
	public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
		return new JwtAuthenticationEntryPoint();
	}

	@Bean
	public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
		return new JwtAccessDeniedHandler();
	}

	@Bean
	@ConditionalOnMissingBean(LogoutSuccessHandler.class)
	public LogoutSuccessHandler jwtLogoutSuccessHandler() {
		return new JwtLogoutSuccessHandler();
	}
}
