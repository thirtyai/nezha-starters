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
package com.thirtyai.nezha.db.config;

import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.thirtyai.nezha.db.tenant.ITenantId;
import com.thirtyai.nezha.db.tenant.NezhaTenantHandler;
import com.thirtyai.nezha.db.tenant.NezhaTenantProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tenant configuration
 *
 * @author kyleju
 */
@Configuration
@EnableConfigurationProperties(NezhaTenantProperties.class)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "nezha.starters",value = "tenant",havingValue = "true")
public class TenantConfiguration {
	private final NezhaTenantProperties tenantProperties;
	private final ObjectProvider<ITenantId> iTenantIdObjectProvider;

	@Bean
	@ConditionalOnMissingBean(TenantHandler.class)
	public TenantHandler tenantHandler() {
		return new NezhaTenantHandler(tenantProperties, iTenantIdObjectProvider.getIfAvailable());
	}
}
