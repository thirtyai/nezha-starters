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
package com.thirtyai.nezha.core.web.config;

import com.thirtyai.nezha.core.web.exception.NezhaErrorViewResolver;
import com.thirtyai.nezha.core.web.filter.NezhaWebFilter;
import com.thirtyai.nezha.core.web.props.NezhaWebProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * nezha web mvc configurer
 *
 * @author kyleju
 */
@Configuration
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(NezhaWebProperties.class)
@ComponentScan({"com.thirtyai.nezha"})
public class NezhaCoreWebConfiguration {

	public final int CORS_FILTER_ORDER_STEP = 100;

	public final String CORS_FILTER_NAME = "nezha-cors-filter";

	private final NezhaWebProperties nezhaWebProperties;

	NezhaCoreWebConfiguration(NezhaWebProperties nezhaWebProperties) {
		this.nezhaWebProperties = nezhaWebProperties;
	}

	@Bean
	@ConditionalOnProperty(prefix = "nezha.web", name = "cors", havingValue = "true")
	public CorsFilter corsFilter() {
		log.info("///nezha/// nezha.web.cors is true.");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig());
		return new CorsFilter(source);
	}

	/**
	 * create Nezha web filter.
	 *
	 * @return ignore
	 */
	@Bean
	public NezhaWebFilter nezhaWebFilter() {
		return new NezhaWebFilter(nezhaWebProperties);
	}

	@Bean
	public FilterRegistrationBean<NezhaWebFilter> nezhaWebFilterRegistration(NezhaWebFilter nezhaWebFilter) {
		FilterRegistrationBean<NezhaWebFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(nezhaWebFilter);
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setName(NezhaWebFilter.NEZHA_WEB_FILTER_NAME);
		filterRegistrationBean.setOrder(nezhaWebProperties.getFilterOrder() + NezhaWebFilter.NEZHA_WEB_FILTER_ORDER_STEP);
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> coreWebFilterRegistration(CorsFilter corsFilter) {
		FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(corsFilter);
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setName(CORS_FILTER_NAME);
		filterRegistrationBean.setOrder(nezhaWebProperties.getFilterOrder() + CORS_FILTER_ORDER_STEP);
		return filterRegistrationBean;
	}

	@Bean
	public NezhaErrorViewResolver conventionErrorViewResolver(ApplicationContext applicationContext, ObjectProvider<ResourceProperties> resourcePropertiesObjectProvider) {
		return new NezhaErrorViewResolver(applicationContext, resourcePropertiesObjectProvider.getIfAvailable());
	}

	/**
	 * build config
	 *
	 * @return ignore
	 */
	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		return corsConfiguration;
	}

}
