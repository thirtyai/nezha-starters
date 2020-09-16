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
package com.thirtyai.nezha.nacos.converter;

import com.alibaba.nacos.api.config.convert.NacosConfigConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirtyai.nezha.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Nacos Json Converter
 *
 * @author kyleju
 */
@Slf4j
@SuppressWarnings("unchecked")
public class NacosJsonConverter<T> implements NacosConfigConverter<T> {

	private ObjectMapper objectMapper;

	@Override
	public boolean canConvert(Class<T> targetType) {
		return objectMapper().canSerialize(targetType);
	}

	@Override
	public T convert(String config) {
		try {
			Type genericSuperclass = this.getClass().getGenericSuperclass();
			ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			Class<T> tClass = (Class<T>) actualTypeArguments[0];
			return objectMapper().readValue(config, tClass);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private ObjectMapper objectMapper() {
		if (this.objectMapper == null) {
			this.objectMapper = JsonUtil.getObjectMapperInstance();
			return JsonUtil.setJsonObjectMapperParams(objectMapper);
		}
		return this.objectMapper;
	}
}
