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
package com.thirtyai.nezha.nacos.data;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.thirtyai.nezha.common.NezhaConstant;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kyleju
 */
@Data
@NacosConfigurationProperties(prefix = "data",dataId = NezhaConstant.GLOBAL_DYNAMIC_CONFIG_DATA_ID, groupId = NezhaConstant.GLOBAL_DYNAMIC_CONFIG_GROUP, type = ConfigType.YAML, autoRefreshed = true)
public class GlobalDynamicData {

	/**
	 * name
	 */
	private String name;

	/**
	 * version
	 */
	private String version;

	/**
	 * cors
	 */
	private Cors cors;

	/**
	 * kv 配合
	 */
	private Map<String, String> kvs = new HashMap<>();


	/**
	 * allowed origin
	 */
	@Data
	public static class Cors implements Serializable{
		private String allowedOrigin;
	}
}
