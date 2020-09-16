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
package com.thirtyai.nezha.nacos.operator;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.thirtyai.nezha.nacos.data.GlobalDynamicData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kyleju
 */
@Slf4j
@RequiredArgsConstructor
public class NacosOperator {
	/**
	 * global dynamic data
	 */
	private final GlobalDynamicData globalDynamicData;
	/**
	 * nacos operator maintained
	 */
	private final ConfigService configService;

	/**
	 * get global dynamic data
	 *
	 * @return global dynamic data {@link GlobalDynamicData}
	 */
	public GlobalDynamicData getGlobalDynamicData() {
		return this.globalDynamicData;
	}

	public String getConfig(String dataId, String group, long timeoutMs) throws NacosException {
		return this.configService.getConfig(dataId, group, timeoutMs);
	}

	public String getConfigAndSignListener(String dataId, String group, long timeoutMs, Listener listener) throws NacosException {
		return this.configService.getConfigAndSignListener(dataId, group, timeoutMs, listener);
	}

	public void addListener(String dataId, String group, Listener listener) throws NacosException {
		this.configService.addListener(dataId, group, listener);
	}

	public boolean publishConfig(String dataId, String group, String content) throws NacosException {
		return this.configService.publishConfig(dataId, group, content);
	}

	public boolean removeConfig(String dataId, String group) throws NacosException {
		return this.configService.removeConfig(dataId, group);
	}

	public void removeListener(String dataId, String group, Listener listener) {
		this.configService.removeListener(dataId, group, listener);
	}

	public String getServerStatus() {
		return this.configService.getServerStatus();
	}
}
