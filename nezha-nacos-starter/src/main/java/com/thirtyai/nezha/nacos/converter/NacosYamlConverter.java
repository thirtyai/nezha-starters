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
package com.thirtyai.nezha.nacos.converter;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.api.config.annotation.NacosIgnore;
import com.alibaba.nacos.api.config.annotation.NacosProperty;
import com.alibaba.nacos.api.config.convert.NacosConfigConverter;
import com.alibaba.nacos.spring.util.ConfigParseUtils;
import com.thirtyai.nezha.common.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;
import static org.springframework.util.StringUtils.hasText;

/**
 * nacos yaml converter
 *
 * @author kyleju
 */
@Slf4j
@SuppressWarnings("unchecked")
public class NacosYamlConverter<T> implements NacosConfigConverter<T> {
	private final static String NACOS_YAML_CONVERTER_STANDARD_ENVIRONMENT = "nacos_yaml_converter_standard_environment";

	@Override
	public boolean canConvert(Class<T> targetType) {
		return true;
	}

	@Override
	public T convert(String config) {
		Properties properties = ConfigParseUtils.toProperties(config, ConfigType.YAML.getType());
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		Class<T> tClass = (Class<T>) actualTypeArguments[0];
		NacosConfigurationProperties nacosConfigurationProperties = AnnotationUtils.findAnnotation(tClass, NacosConfigurationProperties.class);
		T object = SpringUtil.getBean(tClass);
		if (nacosConfigurationProperties != null && SpringUtil.getApplicationContext() != null) {
			doBind(object, nacosConfigurationProperties, properties, ResolvableType.forClass(tClass));
		} else {
			object = null;
		}
		return object;
	}

	/**
	 * ObjectUtils.cleanMapOrCollectionField(bean);
	 * DataBinder dataBinder = new DataBinder(bean);
	 * dataBinder.setAutoGrowNestedPaths(properties.ignoreNestedProperties());
	 * dataBinder.setIgnoreInvalidFields(properties.ignoreInvalidFields());
	 * dataBinder.setIgnoreUnknownFields(properties.ignoreUnknownFields());
	 * dataBinder.bind(propertyValues);
	 *
	 * @param bean           bean
	 * @param properties     properties
	 * @param propertyValues property values
	 * @param type           type
	 */
	private void doBind(Object bean, NacosConfigurationProperties properties,
						Properties propertyValues, ResolvableType type) {
		StandardEnvironment standardEnvironment = new StandardEnvironment();
		standardEnvironment.getPropertySources().addLast(new PropertiesPropertySource(NACOS_YAML_CONVERTER_STANDARD_ENVIRONMENT, propertyValues));
		Binder binder = Binder.get(standardEnvironment);
		Bindable<?> target = Bindable.of(type).withExistingValue(bean);
		binder.bind(properties.prefix(), target);
		standardEnvironment.getPropertySources().remove(NACOS_YAML_CONVERTER_STANDARD_ENVIRONMENT);
	}

	private PropertyValues resolvePropertyValues(Class<?> type, final String prefix, Properties configProperties) {
		final MutablePropertyValues propertyValues = new MutablePropertyValues();
		ReflectionUtils.doWithFields(type,
			field -> {
				String propertyName = resolvePropertyName(field);
				propertyName = StringUtils.isEmpty(prefix) ? propertyName
					: prefix + "." + propertyName;
				if (hasText(propertyName)) {
					// If it is a map, the data will not be fetched
					if (Collection.class.isAssignableFrom(field.getType())
						|| Map.class.isAssignableFrom(field.getType())) {
						bindContainer(prefix, propertyName, configProperties,
							propertyValues);
						return;
					}
					if (configProperties.containsKey(propertyName)) {
						String propertyValue = configProperties
							.getProperty(propertyName);
						propertyValues.add(field.getName(), propertyValue);
					}
				}
			});
		return propertyValues;
	}

	private String resolvePropertyName(Field field) {
		// Ignore property name if @NacosIgnore present
		if (getAnnotation(field, NacosIgnore.class) != null) {
			return null;
		}
		NacosProperty nacosProperty = getAnnotation(field, NacosProperty.class);
		// If @NacosProperty present ,return its value() , or field name
		return nacosProperty != null ? nacosProperty.value() : field.getName();
	}

	private void bindContainer(String prefix, String fieldName,
							   Properties configProperties, MutablePropertyValues propertyValues) {
		String regx1 = fieldName + "\\[(.*)\\]";
		String regx2 = fieldName + "\\..*";
		Pattern pattern1 = Pattern.compile(regx1);
		Pattern pattern2 = Pattern.compile(regx2);
		Enumeration<String> enumeration = (Enumeration<String>) configProperties
			.propertyNames();
		while (enumeration.hasMoreElements()) {
			String s = enumeration.nextElement();
			String name = StringUtils.isEmpty(prefix) ? s : s.replace(prefix + ".", "");
			String value = configProperties.getProperty(s);
			if (configProperties.containsKey(fieldName)) {
				// for example: list=1,2,3,4,5 will be into here
				bindContainer(prefix, fieldName, listToProperties(fieldName,
					configProperties.getProperty(fieldName)), propertyValues);
			} else if (pattern1.matcher(s).find()) {
				propertyValues.add(name, value);
			} else if (pattern2.matcher(s).find()) {
				int index = s.indexOf('.');
				if (index != -1) {
					String key = s.substring(index + 1);
					propertyValues.add(s.substring(0, index) + "[" + key + "]", value);
				}
			}
		}
	}

	private Properties listToProperties(String fieldName, String content) {
		String[] splits = content.split(",");
		int index = 0;
		Properties properties = new Properties();
		for (String s : splits) {
			properties.put(fieldName + "[" + index + "]", s.trim());
			index++;
		}
		return properties;
	}
}
