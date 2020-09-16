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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * spring util
 *
 * @author kyleju
 */
@Slf4j
@SuppressWarnings("unchecked")
public class SpringUtil {

	@Getter
	private static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringUtil.applicationContext = context;
	}

	public static <T> T getBean(Class<T> clazz) {
		if( applicationContext == null){
			return null;
		}
		if (clazz == null) {
			return null;
		}
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String beanId) {
		if( applicationContext == null){
			return null;
		}
		if (beanId == null) {
			return null;
		}
		return (T) applicationContext.getBean(beanId);
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		if( applicationContext == null){
			return null;
		}
		if (null == beanName || "".equals(beanName.trim())) {
			return null;
		}
		if (clazz == null) {
			return null;
		}
		return (T) applicationContext.getBean(beanName, clazz);
	}

	public static void publishEvent(ApplicationEvent event) {
		if (applicationContext == null) {
			return;
		}
		try {
			applicationContext.publishEvent(event);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

}
