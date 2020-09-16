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
package com.thirtyai.nezha.common;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * nezha constant
 *
 * @author kyleju
 */
public final class NezhaConstant {
	public static final String NEZHA_PREFIX = "nezha" + StrUtil.C_UNDERLINE;

	public static String nezhaPreFix(String key) {
		return NEZHA_PREFIX + key;
	}

	public static final String NEZHA_YML_FILE_EXT_NAME = ".yml";
	public static final String NEZHA_FILE_NAME = "nezha" + NEZHA_YML_FILE_EXT_NAME;
	public static final String CORE_PROPERTIES_YML_FILENAME = "classpath:/" + NEZHA_FILE_NAME;

	/**
	 * resp object's total default value
	 */
	public static final int DEFAULT_VALUE = -1;

	/**
	 * about date format
	 */
	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);

	/**
	 * default static files for all filters
	 */
	public static final String[] DEFAULT_STATIC_EXCLUSIONS = new String[]{"/", "/**/*.txt", "/favicon.ico", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/**/*.gif", "/**/*.jpg", "/**/*.bmp", "/**/*.png", "/**/*.ico", "/webjars/**", "/swagger-resources/**", "/v2/api-docs"};
	/**
	 * default authorization header key
	 */
	public static final String DEFAULT_AUTHORIZATION_HEADER_KEY = "Authorization";

	/**
	 * default task executor
	 */
	public static final String DEFAULT_TASK_EXECUTOR = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
	/**
	 * nezha global dynamic config data id
	 */
	public static final String GLOBAL_DYNAMIC_CONFIG_DATA_ID = "${nezha.globalDataId:nezha-global-data}";
	/**
	 * nezha global dynamic config group
	 */
	public static final String GLOBAL_DYNAMIC_CONFIG_GROUP = "${nezha.globalGroup:DEFAULT_GROUP}";
	/**
	 * nezha log properties config data id
	 */
	public static final String LOG_PROPERTIES_DATA_ID = "${nezha.log.logDataId:nezha-log-config}";
	/**
	 * nezha log properties config group
	 */
	public static final String LOG_PROPERTIES_CONFIG_GROUP = "${nezha.log.logGroup:DEFAULT_GROUP}";
	/**
	 * nacos listener default time out
	 */
	public static final long NACOS_LISTENER_DEFAULT_TIME_OUT = 5000L;

	public static class JavaTimeModule extends SimpleModule {
		public JavaTimeModule() {
			super(PackageVersion.VERSION);
			this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(NezhaConstant.DATETIME_FORMATTER));
			this.addDeserializer(LocalDate.class, new LocalDateDeserializer(NezhaConstant.DATE_FORMATTER));
			this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(NezhaConstant.TIME_FORMATTER));
			this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(NezhaConstant.DATETIME_FORMATTER));
			this.addSerializer(LocalDate.class, new LocalDateSerializer(NezhaConstant.DATE_FORMATTER));
			this.addSerializer(LocalTime.class, new LocalTimeSerializer(NezhaConstant.TIME_FORMATTER));
		}
	}
}
