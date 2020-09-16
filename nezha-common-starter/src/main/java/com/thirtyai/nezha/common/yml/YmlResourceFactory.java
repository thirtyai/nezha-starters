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
package com.thirtyai.nezha.common.yml;

import cn.hutool.core.io.FileUtil;
import com.thirtyai.nezha.common.NezhaConstant;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * YmlResourceFactory
 * yml resource load
 *
 * @author kyleju
 */
public class YmlResourceFactory extends DefaultPropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
		String sourceName = (name == null) ? resource.getResource().getFilename() : name;
		assert sourceName != null;
		if (FileUtil.exist(sourceName)) {
			if (sourceName.endsWith(NezhaConstant.NEZHA_YML_FILE_EXT_NAME)) {
				YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
				factory.setResources(resource.getResource());
				factory.afterPropertiesSet();
				Properties properties = factory.getObject();
				if (Objects.isNull(properties)) {
					properties = new Properties();
				}
				properties = getProperties(properties, sourceName, resource);
				return new PropertiesPropertySource(sourceName, properties);
			} else {
				return super.createPropertySource(name, resource);
			}
		}
		// not exist
		return new PropertiesPropertySource(sourceName, getProperties(new Properties(), sourceName, resource));
	}

	private Properties getProperties(final Properties properties, String sourceName, EncodedResource resource) {
		if (sourceName.endsWith(NezhaConstant.NEZHA_YML_FILE_EXT_NAME)) {
			Properties props = System.getProperties();
			String profile = props.getProperty("spring.profiles.active");
			String sourceNameProfile = sourceName.replace(NezhaConstant.NEZHA_YML_FILE_EXT_NAME, "-" + profile + NezhaConstant.NEZHA_YML_FILE_EXT_NAME);
			if (FileUtil.exist(sourceNameProfile)) {
				YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
				ClassPathResource classPathResource = new ClassPathResource(sourceNameProfile);
				factory.setResources(classPathResource);
				factory.afterPropertiesSet();
				Properties propertiesProfile = factory.getObject();
				propertiesProfile.forEach((key, value) -> {
					properties.setProperty(key.toString(), value.toString());
				});
			}
		}
		return properties;
	}
}
