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
package com.thirtyai.nezha.gateway.config;

import com.thirtyai.nezha.gateway.listener.DynamicGatewayRouteDefinitionListener;
import com.thirtyai.nezha.gateway.props.NezhaGatewayProperties;
import com.thirtyai.nezha.nacos.operator.NacosOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gateway configuration
 *
 * @author kyleju
 */
@Configuration
@EnableConfigurationProperties({NezhaGatewayProperties.class})
@AutoConfigureBefore({GatewayAutoConfiguration.class, ErrorWebFluxAutoConfiguration.class})
@RequiredArgsConstructor
public class GatewayConfiguration {

	@Bean
	@ConditionalOnBean(NacosOperator.class)
	@ConditionalOnProperty(value = "nezha.gateway.dynamic", havingValue = "true")
	public RouteDefinitionRepository dynamicGatewayPropertiesListener() {
		return new DynamicGatewayRouteDefinitionListener();
	}
}
