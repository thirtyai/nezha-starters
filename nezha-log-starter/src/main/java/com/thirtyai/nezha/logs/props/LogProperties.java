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
package com.thirtyai.nezha.logs.props;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.thirtyai.nezha.common.NezhaConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author kyleju
 */
@ConfigurationProperties(LogProperties.LOG_PROPERTIES_PRE_FIX)
@Data
@NacosConfigurationProperties(prefix = LogProperties.LOG_PROPERTIES_PRE_FIX, dataId = NezhaConstant.LOG_PROPERTIES_DATA_ID, groupId = NezhaConstant.LOG_PROPERTIES_CONFIG_GROUP, type = ConfigType.YAML, autoRefreshed = true)
public class LogProperties {
	public static final String LOG_PROPERTIES_PRE_FIX = "nezha.log";
	/**
	 * enable elk
	 */
	private boolean elk = false;
	/**
	 * elk address
	 */
	private String elkAddress;
	/**
	 * logger levels
	 */
	private List<LoggerLevel> loggerLevels;

	@Data
	public static class LoggerLevel implements Serializable {
		private String logger;
		private String level;
	}
}
