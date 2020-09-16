/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.webflux.config;

import com.thirtyai.nezha.webflux.exception.NezhaErrorAttributes;
import com.thirtyai.nezha.webflux.exception.NezhaErrorWebExceptionHandler;
import com.thirtyai.nezha.webflux.props.NezhaWebfluxProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.stream.Collectors;

/**
 * webflux configuration
 *
 * @author kyleju
 */
@Configuration
@RequiredArgsConstructor
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
@EnableConfigurationProperties({NezhaWebfluxProperties.class, ServerProperties.class, ResourceProperties.class})
public class WebfluxConfiguration {
	private final ServerProperties serverProperties;
	private final ResourceProperties resourceProperties;
	private final ServerCodecConfigurer serverCodecConfigurer;

	@Bean
	public NezhaErrorAttributes errorAttributes() {
		return new NezhaErrorAttributes(this.serverProperties.getError().isIncludeException());
	}

	@Bean
	@Order(-1)
	public NezhaErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes,
																  ObjectProvider<ViewResolver> viewResolvers,
																  ApplicationContext applicationContext) {
		NezhaErrorWebExceptionHandler exceptionHandler = new NezhaErrorWebExceptionHandler(errorAttributes,
			resourceProperties, this.serverProperties.getError(), applicationContext);
		exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
		exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
		exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
		return exceptionHandler;
	}
}
