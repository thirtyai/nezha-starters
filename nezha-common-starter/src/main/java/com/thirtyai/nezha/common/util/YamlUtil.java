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
package com.thirtyai.nezha.common.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.InputStream;

/**
 * yaml util
 *
 * @author kyleju
 */
@Slf4j
@SuppressWarnings("unchecked")
public class YamlUtil {

	/**
	 * parse yaml file
	 * @param filePath file path
	 * @param valueType value type
	 * @param <T> T object
	 * @return ignore
	 */
	public static <T> T parse(String filePath, Class<T> valueType) {
		try {
			if (FileUtil.exist(FileUtil.getAbsolutePath(filePath))) {
				return parse(new File(FileUtil.getAbsolutePath(filePath)), valueType);
			}
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * parse yaml file
	 * @param file file
	 * @param valueType value type
	 * @param <T> T
	 * @return ignore
	 */
	public static <T> T parse(File file, Class<T> valueType) {
		try {
			if (file != null) {
				return parse(FileUtil.getInputStream(file), valueType);
			}
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * parse yaml file
	 * @param inputStream input stream {@link InputStream}
	 * @param valueType value type
	 * @param <T> T
	 * @return ignore
	 */
	public static <T> T parse(InputStream inputStream, Class<T> valueType) {
		try {
			if (inputStream != null) {
				Representer representer = new Representer();
				representer.getPropertyUtils().setSkipMissingProperties(true);
				Yaml yaml = new Yaml(new Constructor(valueType), representer);
				T temp = null;
				for (Object o : yaml.loadAll(inputStream)) {
					if (o.getClass().equals(valueType)) {
						temp = (T) o;
					}
				}
				return temp;
			}
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
