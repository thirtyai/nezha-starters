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
package com.thirtyai.nezha.gateway.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.common.util.SpringUtil;
import com.thirtyai.nezha.gateway.props.NezhaGatewayProperties;
import com.thirtyai.nezha.gateway.props.NezhaGatewayPropertiesConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Dynamic router service
 *
 * @author kyleju
 */
@Slf4j
public class DynamicGatewayRouteDefinitionListener implements RouteDefinitionRepository {

	private List<RouteDefinition> routeDefinitions;
	/**
	 * update list
	 *
	 * @param routeDefinitions route definitions {@link List<RouteDefinition>}
	 */
	private void updateList(List<RouteDefinition> routeDefinitions) {
		if (routeDefinitions != null && routeDefinitions.size() > 0) {
			this.routeDefinitions = routeDefinitions;
			SpringUtil.publishEvent(new RefreshRoutesEvent(this));
		}
	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		if (this.routeDefinitions == null) {
			this.routeDefinitions = CollUtil.newArrayList();
		}
		return Flux.fromIterable(this.routeDefinitions);
	}

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return null;
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return null;
	}

	@NacosConfigListener(dataId = NezhaGatewayProperties.GATEWAY_DATA_ID, groupId = NezhaGatewayProperties.GATEWAY_GROUP_ID, type = ConfigType.YAML, converter = NezhaGatewayPropertiesConverter.class, timeout = NezhaConstant.NACOS_LISTENER_DEFAULT_TIME_OUT)
	public void onChange(NezhaGatewayProperties nezhaGatewayProperties) {
		if (nezhaGatewayProperties != null && CollUtil.isNotEmpty(nezhaGatewayProperties.getRoutes())) {
			this.updateList(nezhaGatewayProperties.getRoutes());
		}
	}
}
