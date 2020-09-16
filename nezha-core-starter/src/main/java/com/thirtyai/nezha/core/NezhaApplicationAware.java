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
package com.thirtyai.nezha.core;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.NoResourceException;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.common.util.YamlUtil;
import lombok.Data;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.Aware;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

/**
 * nezha application aware
 *
 * @author kyleju
 */
public interface NezhaApplicationAware extends Aware {
	/**
	 * get app name
	 * will overwrite Spring application name
	 *
	 * @param profile profile
	 * @return ignore
	 */
	default String getAppName(String profile) {
		SpringWrap spring = null;
		ClassPathResource classPathResource;
		try {
			classPathResource = new ClassPathResource(NezhaConstant.NEZHA_FILE_NAME.replace(NezhaConstant.NEZHA_YML_FILE_EXT_NAME, "-" + profile + NezhaConstant.NEZHA_YML_FILE_EXT_NAME));
		} catch (NoResourceException ex) {
			classPathResource = null;
		}

		if (classPathResource != null) {
			spring = YamlUtil.parse(classPathResource.getStream(), SpringWrap.class);
		}

		if (spring != null && spring.getSpring() != null && spring.getSpring().getApplication() != null) {
			return spring.getSpring().getApplication().getName();
		} else {
			try {
				classPathResource = new ClassPathResource(NezhaConstant.NEZHA_FILE_NAME);
			} catch (NoResourceException ex) {
				classPathResource = null;
			}
			if (classPathResource != null) {
				spring = YamlUtil.parse(classPathResource.getStream(), SpringWrap.class);
			}
			if (spring != null && spring.getSpring() != null && spring.getSpring().getApplication() != null) {
				return spring.getSpring().getApplication().getName();
			}
		}

		return null;
	}

	/**
	 * set system properties
	 *
	 * @param props properties {@link Properties}
	 */
	default void setSystemProperties(@NotNull Properties props) {
	}

	/**
	 * config spring boot builder
	 * @param springApplicationBuilder builder {@link SpringApplicationBuilder}
	 */
	default void configSpringBootBuilder(SpringApplicationBuilder springApplicationBuilder) {

	}

	@Data
	class SpringWrap {
		private Spring spring;
	}

	@Data
	class Spring {
		private SpringApp application;
	}

	@Data
	class SpringApp {
		private String name;
	}
}
