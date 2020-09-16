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
package com.thirtyai.nezha.logs.config;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.logs.props.LogProperties;
import com.thirtyai.nezha.logs.props.LogPropertiesYamlConverter;
import com.thirtyai.nezha.nacos.converter.NacosYamlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Configuration;

/**
 * @author kyleju
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = "nezha.starters.log", name = "nacos", havingValue = "true")
@RequiredArgsConstructor
public class LogStarterConfig {

	private final LoggingSystem loggingSystem;

	@NacosConfigListener(dataId = NezhaConstant.LOG_PROPERTIES_DATA_ID, groupId = NezhaConstant.LOG_PROPERTIES_CONFIG_GROUP, converter = LogPropertiesYamlConverter.class,  type = ConfigType.YAML, timeout = NezhaConstant.NACOS_LISTENER_DEFAULT_TIME_OUT)
	public void onChange(LogProperties logProperties) {
		if (logProperties != null && CollUtil.isNotEmpty(logProperties.getLoggerLevels())) {
			logProperties.getLoggerLevels().forEach(item -> {
				LogLevel level = LogLevel.valueOf(item.getLevel().toUpperCase());
				loggingSystem.setLogLevel(item.getLogger(), level);
			});
		}
	}
}
