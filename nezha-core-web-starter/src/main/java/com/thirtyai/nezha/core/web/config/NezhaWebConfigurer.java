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
package com.thirtyai.nezha.core.web.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirtyai.nezha.core.web.converter.MappingRequestModifyResponseJackson2HttpMessageConverter;
import com.thirtyai.nezha.core.web.props.NezhaWebProperties;
import com.thirtyai.nezha.common.util.JsonUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Nezha web configurer
 * http message converter, i18n resolver config
 *
 * @author kyleju
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NezhaWebConfigurer implements WebMvcConfigurer {
	private final ObjectMapper objectMapper;
	private final NezhaWebProperties webProperties;

	public NezhaWebConfigurer(ObjectProvider<ObjectMapper> objectMapperObjectProvider, NezhaWebProperties webProperties) {
		ObjectMapper objectMapperTemp = objectMapperObjectProvider.getIfAvailable();
		this.objectMapper = JsonUtil.setJsonObjectMapperParams(objectMapperTemp);
		this.webProperties = webProperties;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.removeIf(x -> x instanceof StringHttpMessageConverter || x instanceof AbstractJackson2HttpMessageConverter);
		converters.add(new MappingRequestModifyResponseJackson2HttpMessageConverter(objectMapper));
		converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new ResourceHttpMessageConverter());
		converters.add(new ResourceRegionHttpMessageConverter());
	}

	/**
	 * date & datetime formatter
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new DateFormatter(DatePattern.NORM_DATE_PATTERN));
		registry.addFormatter(new DateFormatter(DatePattern.NORM_DATETIME_PATTERN));
	}
}
