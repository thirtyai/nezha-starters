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
package com.thirtyai.nezha.gateway.props;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.thirtyai.nezha.gateway.listener.DynamicGatewayRouteDefinitionListener;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

/**
 * nezha gateway properties
 *
 * @author kyleju
 */
@Data
@ConfigurationProperties("nezha.gateway")
@NacosConfigurationProperties(prefix = "nezha.gateway", dataId = NezhaGatewayProperties.GATEWAY_DATA_ID, groupId = NezhaGatewayProperties.GATEWAY_GROUP_ID, type = ConfigType.YAML)
@RequiredArgsConstructor
public class NezhaGatewayProperties implements InitializingBean {
	public static final String GATEWAY_DATA_ID = "${nezha.gateway.dataId:nezha-gateway-config}";
	public static final String GATEWAY_GROUP_ID = "${nezha.gateway.groupId:DEFAULT_GROUP}";
	private final ObjectProvider<DynamicGatewayRouteDefinitionListener> dynamicGatewayPropertiesListener;

	/**
	 * routes {@link List<RouteDefinition>}
	 */
	@NestedConfigurationProperty
	private List<RouteDefinition> routes;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dynamicGatewayPropertiesListener != null) {
			dynamicGatewayPropertiesListener.ifAvailable(listener -> {
				listener.onChange(this);
			});
		}
	}
}
