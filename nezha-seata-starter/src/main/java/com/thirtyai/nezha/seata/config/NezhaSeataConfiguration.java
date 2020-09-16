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
package com.thirtyai.nezha.seata.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.thirtyai.nezha.seata.props.NezhaSeataProperties;
import com.thirtyai.nezha.common.condition.ConditionalOnPropertyMultipleValues;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.xa.DataSourceProxyXA;
import io.seata.spring.annotation.GlobalTransactionScanner;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.FailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * seata configuration
 * XA model，when use hikari pool, throw "object is not an instance of declaring class error". druid pool is ok.
 * AT model, druid & hikari all is ok.
 *
 * @author kyleju
 */
@Configuration
@EnableConfigurationProperties({NezhaSeataProperties.class, DataSourceProperties.class})
@ConditionalOnPropertyMultipleValues(prefix = "nezha.starters", value = "seata", havingValues = {"AT", "XA"})
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class NezhaSeataConfiguration {
	private static final String DRUID_CLASS = "com.alibaba.druid.pool.DruidDataSource";
	private static final String HIKARI_CLASS = "com.zaxxer.hikari.HikariDataSource";

	/**
	 * create data source
	 *
	 * @param properties datasource properties {@link DataSourceProperties}
	 * @param type       type {@link DataSource}
	 * @param <T>
	 * @return value
	 */
	private static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
		return (T) properties.initializeDataSourceBuilder().type(type).build();
	}

	public final DataSourceProperties properties;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	private HikariDataSource hikariDataSource() {
		HikariConfig hikariConfig = hikariConfig();
		HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
		hikariConfig.setDriverClassName(dataSource.getDriverClassName());
		hikariConfig.setDataSourceClassName(dataSource.getDataSourceClassName());
		hikariConfig.setJdbcUrl(dataSource.getJdbcUrl());
		hikariConfig.setUsername(dataSource.getUsername());
		hikariConfig.setPassword(dataSource.getPassword());
		hikariConfig.copyStateTo(dataSource);
		dataSource.validate();
		if (StringUtils.hasText(properties.getName())) {
			dataSource.setPoolName(properties.getName());
		}
		return dataSource;
	}

	private DruidDataSource druidDataSource() {
		DruidDataSource dataSource = createDataSource(properties, DruidDataSource.class);
		if (StringUtils.hasText(properties.getName())) {
			dataSource.setName(properties.getName());
		}
		return dataSource;
	}

	@Bean
	public DataSource dataSource() {
		if (DRUID_CLASS.equals(properties.getType().getName())) {
			return druidDataSource();
		} else {
			if (HIKARI_CLASS.equals(properties.getType().getName())) {
				return hikariDataSource();
			}
		}
		return null;
	}

	/**
	 * init datasource proxy
	 *
	 * @return DataSourceProxy  datasource proxy
	 */
	@Bean("dataSourceProxy")
	@ConditionalOnProperty(prefix = "nezha.starters", value = "seata", havingValue = "AT")
	public DataSourceProxy dataSourceProxy() {
		return new DataSourceProxy(dataSource());
	}

	@Bean("dataSourceProxy")
	@ConditionalOnProperty(prefix = "nezha.starters", value = "seata", havingValue = "XA")
	public DataSourceProxyXA dataSourceProxyXA() {
		return new DataSourceProxyXA(dataSource());
	}

	/**
	 * data source transaction manager
	 *
	 * @return value {@link DataSourceTransactionManager}
	 */
	@Bean("transactionManager")
	@ConditionalOnProperty(prefix = "nezha.starters", value = "seata", havingValue = "AT")
	public DataSourceTransactionManager transactionManagerAT() {
		return new DataSourceTransactionManager(dataSourceProxy());
	}

	@Bean("transactionManager")
	@ConditionalOnProperty(prefix = "nezha.starters", value = "seata", havingValue = "XA")
	public DataSourceTransactionManager transactionManagerXA() {
		return new DataSourceTransactionManager(dataSourceProxyXA());
	}

	@Bean
	@ConditionalOnMissingBean(FailureHandler.class)
	public FailureHandler failureHandler() {
		return new DefaultFailureHandlerImpl();
	}

	@Bean
	@ConditionalOnMissingBean(GlobalTransactionScanner.class)
	public GlobalTransactionScanner globalTransactionScanner(NezhaSeataProperties nezhaSeataProperties, FailureHandler failureHandler) {
		return new GlobalTransactionScanner(nezhaSeataProperties.getApplicationName(), nezhaSeataProperties.getTxServiceGroup(), failureHandler);
	}

}
