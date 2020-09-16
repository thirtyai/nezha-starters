/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.nacos.config;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.spring.context.annotation.EnableNacos;
import com.thirtyai.nezha.nacos.data.GlobalDynamicData;
import com.thirtyai.nezha.nacos.operator.NacosOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kyleju
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "nezha.starters", name = "nacos", havingValue = "true")
@EnableNacos(globalProperties = @NacosProperties())
public class NezhaNacosConfig {
	@NacosInjected
	private ConfigService configService;

	@Bean
	public GlobalDynamicData globalRemoteData() {
		return new GlobalDynamicData();
	}
	
	@Bean
	public NacosOperator nacosOperator() {
		return new NacosOperator(globalRemoteData(), configService);
	}
}

