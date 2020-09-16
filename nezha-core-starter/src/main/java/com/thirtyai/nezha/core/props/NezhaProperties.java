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
package com.thirtyai.nezha.core.props;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * nezha properties
 *
 * @author kyleju
 */
@Data
@Slf4j
@ConfigurationProperties("nezha")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NezhaProperties implements EnvironmentAware, EnvironmentCapable, InitializingBean {

	private String dev;

	public NezhaProperties() {

	}

	@Nullable
	private Environment environment;

	private final Starters starters = new Starters();

	/**
	 * nezha.prop.*
	 */
	private final Map<String, String> props = new HashMap<>();

	/**
	 * get config
	 *
	 * @param key key
	 * @return value
	 */
	@Nullable
	public String get(String key) {
		return get(key, null);
	}

	/**
	 * 获取配置
	 *
	 * @param key          key
	 * @param defaultValue default value
	 * @return value
	 */
	@Nullable
	public String get(String key, @Nullable String defaultValue) {
		String value = props.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * get config
	 *
	 * @param key key
	 * @return int value
	 */
	@Nullable
	public Integer getInt(String key) {
		return getInt(key, null);
	}

	/**
	 * get config
	 *
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return int value
	 */
	@Nullable
	public Integer getInt(String key, @Nullable Integer defaultValue) {
		String value = props.get(key);
		if (value != null) {
			return Integer.valueOf(value.trim());
		}
		return defaultValue;
	}

	/**
	 * get config
	 *
	 * @param key key
	 * @return long value
	 */
	@Nullable
	public Long getLong(String key) {
		return getLong(key, null);
	}

	/**
	 * get config
	 *
	 * @param key          key
	 * @param defaultValue default value
	 * @return long value
	 */
	@Nullable
	public Long getLong(String key, @Nullable Long defaultValue) {
		String value = props.get(key);
		if (value != null) {
			return Long.valueOf(value.trim());
		}
		return defaultValue;
	}

	/**
	 * get config
	 *
	 * @param key key
	 * @return Boolean value
	 */
	@Nullable
	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	/**
	 * get config
	 *
	 * @param key          key
	 * @param defaultValue default value
	 * @return Boolean value
	 */
	@Nullable
	public Boolean getBoolean(String key, @Nullable Boolean defaultValue) {
		String value = props.get(key);
		if (value != null) {
			value = value.toLowerCase().trim();
			return Boolean.parseBoolean(value);
		}
		return defaultValue;
	}

	/**
	 * get config
	 *
	 * @param key key
	 * @return double value
	 */
	@Nullable
	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	/**
	 * get config
	 *
	 * @param key          key
	 * @param defaultValue default value
	 * @return double value
	 */
	@Nullable
	public Double getDouble(String key, @Nullable Double defaultValue) {
		String value = props.get(key);
		if (value != null) {
			return Double.parseDouble(value.trim());
		}
		return defaultValue;
	}

	/**
	 * judge the key exist
	 *
	 * @param key prop key
	 * @return boolean
	 */
	public boolean containsKey(String key) {
		return props.containsKey(key);
	}


	/**
	 * get environment
	 *
	 * @return 环境 env
	 */
	public String getEnv() {
		Objects.requireNonNull(environment, "Spring boot cannot be null");
		String env = environment.getProperty("nezha.env");
		Assert.notNull(env, "Please use NezhaApplication start!!!");
		return env;
	}

	/**
	 * application name
	 * ${spring.application.name}
	 *
	 * @return application name
	 */
	public String getAppName() {
		Objects.requireNonNull(environment, "Spring boot cannot be null");
		return environment.getProperty("spring.application.name", "");
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public Environment getEnvironment() {
		Objects.requireNonNull(environment, "Spring boot cannot be null");
		return this.environment;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("///nezha/// core properties are loaded...");
	}

	@Data
	public static class Starters {
		private boolean redis;
		private boolean swagger;
		private boolean nacos;
		private boolean security;
		private boolean i18n;
		private boolean log;
	}
}
