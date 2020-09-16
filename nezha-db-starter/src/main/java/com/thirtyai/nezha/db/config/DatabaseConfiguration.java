/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.db.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

/**
 * Database configuration
 *
 * @author kyleju
 */
@Configuration
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@AutoConfigureAfter({TenantConfiguration.class})
@Import(TenantConfiguration.class)
public class DatabaseConfiguration {

	@Bean
	public PaginationInterceptor paginationInterceptor(ObjectProvider<TenantHandler> tenantHandlerObjectProvider) {
		PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
		List<ISqlParser> sqlParserList = new ArrayList<>();
		sqlParserList.add(new BlockAttackSqlParser());

		tenantHandlerObjectProvider.ifAvailable(tenantHandler -> {
			TenantSqlParser tenantSqlParser = new TenantSqlParser();
			tenantSqlParser.setTenantHandler(tenantHandler);
			sqlParserList.add(tenantSqlParser);
		});

		paginationInterceptor.setSqlParserList(sqlParserList);
		return paginationInterceptor;
	}
}
