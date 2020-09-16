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
package com.thirtyai.nezha.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.thirtyai.nezha.common.NezhaConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

/**
 * json operator
 *
 * @author kyleju
 */
@Slf4j
public class JsonUtil {

	/**
	 * to json
	 * @param value object
	 * @param <T> type
	 * @return string
	 */
	public static <T> String toJson(T value) {
		try {
			return getObjectMapperInstance().writeValueAsString(value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * to json as types
	 * @param object object
	 * @return ignore
	 * @throws JsonProcessingException ignore
	 */
	public static byte[] toJsonAsBytes(Object object) throws JsonProcessingException {
		return getObjectMapperInstance().writeValueAsBytes(object);
	}

	/**
	 * json to object
	 * @param content json string
	 * @param valueType class
	 * @param <T> T
	 * @return object
	 */
	public static <T> T parse(String content, Class<T> valueType) {
		try {
			return getObjectMapperInstance().readValue(content, valueType);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * json to object
	 * @param content json string
	 * @param typeReference type
	 * @param <T> t
	 * @return ignore
	 * @throws JsonProcessingException ignore
	 */
	public static <T> T parse(String content, TypeReference<?> typeReference) throws JsonProcessingException {
		return (T) getObjectMapperInstance().readValue(content, typeReference);
	}

	/**
	 * json bytes to object
	 * @param bytes bytes
	 * @param valueType class
	 * @param <T> T
	 * @return object
	 * @throws IOException exception
	 */
	public static <T> T parse(byte[] bytes, Class<T> valueType) throws IOException {
		return getObjectMapperInstance().readValue(bytes, valueType);
	}


	/**
	 * json bytes to object
	 * @param bytes bytes
	 * @param typeReference class
	 * @param <T> T
	 * @return object
	 * @throws IOException exception
	 */
	public static <T> T parse(byte[] bytes, TypeReference<?> typeReference) throws IOException {
		return (T) getObjectMapperInstance().readValue(bytes, typeReference);
	}

	/**
	 * input stream to json
	 * @param in input stream
	 * @param valueType class
	 * @param <T> T
	 * @return object
	 * @throws IOException exception
	 */
	public static <T> T parse(InputStream in, Class<T> valueType) throws IOException {
		return getObjectMapperInstance().readValue(in, valueType);
	}

	/**
	 * input stream to object
	 * @param in input stream
	 * @param typeReference class
	 * @param <T> T
	 * @return object
	 * @throws IOException exception
	 */
	public static <T> T parse(InputStream in, TypeReference<?> typeReference) throws IOException {
		return (T) getObjectMapperInstance().readValue(in, typeReference);
	}

	/**
	 * json to List
	 *
	 * @param content      content
	 * @param valueTypeRef class
	 * @param <T>          T
	 * @return ignore
	 */
	public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
		try {

			if (!StrUtil.startWithIgnoreCase(content, new StringBuilder(1).append(StrUtil.C_BRACKET_START))) {
				content = StrUtil.C_BRACKET_START + content + StrUtil.C_BRACKET_END;
			}

			List<Map<String, Object>> list = (List<Map<String, Object>>) getObjectMapperInstance().readValue(content, new TypeReference<List<T>>() {
			});
			List<T> result = new ArrayList<>();
			for (Map<String, Object> map : list) {
				result.add(toPojo(map, valueTypeRef));
			}
			return result;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static Map<String, Object> toMap(String content) {
		try {
			return getObjectMapperInstance().readValue(content, Map.class);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
		try {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) getObjectMapperInstance().readValue(content, new TypeReference<Map<String, T>>() {
			});
			Map<String, T> result = new HashMap<>(16);
			for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
				result.put(entry.getKey(), toPojo(entry.getValue(), valueTypeRef));
			}
			return result;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
		return getObjectMapperInstance().convertValue(fromValue, toValueType);
	}

	/**
	 * json string to Json Node
	 * @param jsonString Json String
	 * @return JsonNode {@link JsonNode}
	 * @throws JsonProcessingException exception {@link JsonProcessingException}
	 */
	public static JsonNode readTree(String jsonString) throws JsonProcessingException {
		return getObjectMapperInstance().readTree(jsonString);
	}

	/**
	 * json to JsonNode
	 * @param in input stream
	 * @return JsonNode
	 * @throws IOException exception
	 */
	public static JsonNode readTree(InputStream in) throws IOException {
		return getObjectMapperInstance().readTree(in);
	}

	/**
	 * json to json node
	 * @param content content
	 * @return Json node
	 * @throws IOException exception
	 */
	public static JsonNode readTree(byte[] content) throws IOException {
		return getObjectMapperInstance().readTree(content);
	}

	/**
	 * json to json node
	 * @param jsonParser parser
	 * @return JsonParser json parser
	 * @throws IOException exception
	 */
	public static JsonNode readTree(JsonParser jsonParser) throws IOException {
		return getObjectMapperInstance().readTree(jsonParser);
	}


	/**
	 * read value
	 * @param json  json string
	 * @param parametrized type
	 * @param parametersFor for
	 * @param parameterClasses classes
	 * @param <T> class
	 * @return class
	 */
	public static <T> T readValue(String json, Class<?> parametrized, Class<?> parametersFor,
								  Class<?>... parameterClasses) {
		if (StrUtil.isBlank(json)) {
			return null;
		}

		JavaType type;
		if (parameterClasses == null || parameterClasses.length == 0) {
			type = getObjectMapperInstance().getTypeFactory().constructParametricType(parametrized, parametersFor);
		} else {
			type = getObjectMapperInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
		}

		try {
			return getObjectMapperInstance().readValue(json, type);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static <T> T readMap(String content, Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
		if (StrUtil.isBlank(content)) {
			return null;
		}
		try {
			return getObjectMapperInstance().readValue(content, getObjectMapperInstance().getTypeFactory().constructMapType(mapClass, keyClass, valueClass));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> List<T> readList(String content, Class<?> collectionClass, Class<T> elementClass) {
		if (StrUtil.isBlank(content)) {
			return null;
		}
		try {
			return getObjectMapperInstance().readValue(content, getObjectMapperInstance().getTypeFactory()
				.constructCollectionLikeType(collectionClass == null ? List.class : collectionClass, elementClass));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> List<T> readList(String content, Class<T> elementClass) {
		return readList(content, null, elementClass);
	}

	public static ObjectMapper getObjectMapperInstance() {
		return JacksonHolder.instance();
	}

	private static class JacksonHolder {
		private static ObjectMapper instance() {
			ObjectMapper om = SpringUtil.getBean(ObjectMapper.class);
			return JsonUtil.setJsonObjectMapperParams(om);
		}
	}

	private static class JacksonObjectMapper extends ObjectMapper {

		public JacksonObjectMapper() {
			super();
		}

		@Override
		public ObjectMapper copy() {
			return super.copy();
		}
	}

	public static ObjectMapper setJsonObjectMapperParams(ObjectMapper objectMapper) {
		if (objectMapper == null) {
			objectMapper = new JacksonObjectMapper();
		}

		//设置地点为中国
		objectMapper.setLocale(Locale.CHINA);
		//去掉默认的时间戳格式
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		//设置为中国上海时区
		objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
		//序列化时，日期的统一格式
		objectMapper.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN, Locale.CHINA));
		//序列化处理
		//允许使用未带引号的字段名
		objectMapper.configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true);

		objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		objectMapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
		//失败处理
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//单引号处理
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		//反序列化时，属性不存在的兼容处理
		objectMapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		//日期格式化
		objectMapper.registerModule(new NezhaConstant.JavaTimeModule());
		/*
		 * LinkedHashMap
		 * objectMapper.  activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		 */
		objectMapper.findAndRegisterModules();
		return objectMapper;
	}

	public static String format(String json) {
		// 缩进
		StringBuilder indent = new StringBuilder();
		StringBuilder sb = new StringBuilder();

		for (char c : json.toCharArray()) {
			switch (c) {
				case '{':
					indent.append("\t");
					sb.append("{\n").append(indent);
					break;
				case '}':
					indent.deleteCharAt(indent.length() - 1);
					sb.append("\n").append(indent).append("}");
					break;
				case '[':
					indent.append("\t");
					sb.append("[\n").append(indent);
					break;
				case ']':
					indent.deleteCharAt(indent.length() - 1);
					sb.append("\n").append(indent).append("]");
					break;
				case ',':
					sb.append(",\n").append(indent);
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

}
