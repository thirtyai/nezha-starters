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

import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.common.util.SpringUtil;
import com.thirtyai.nezha.core.props.NezhaProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * nezha starters
 * core-starter main class
 *
 * @author kyleju
 */
@Slf4j
public final class NezhaApplication {

	private static final AtomicBoolean HAD_RESET_SYSTEM_PROPERTIES = new AtomicBoolean(false);

	private static final AtomicBoolean INITIALIZED_NEZHA = new AtomicBoolean(false);

	@Getter
	private static NezhaProperties nezhaProperties;

	@NotNull
	private static NezhaApplicationAware nezhaApplicationAware;

	@Getter
	private static final Banner.Mode BANNER_MODE = Banner.Mode.CONSOLE;

	private static SpringApplicationBuilder createSpringApplicationBuilder(Class<?> source) {
		return new SpringApplicationBuilder(source).bannerMode(Banner.Mode.OFF);
	}

	/**
	 * get app name
	 *
	 * @return app name
	 */
	public static String getAppName(String profile) {
		if (nezhaApplicationAware != null && StrUtil.isNotEmpty(nezhaApplicationAware.getAppName(profile))) {
			return nezhaApplicationAware.getAppName(profile);
		} else if (nezhaProperties != null) {
			return nezhaProperties.getAppName();
		}
		return "";
	}

	public static ConfigurableApplicationContext run(NezhaApplicationAware applicationAware, boolean cloudBootstrapEnabled, Class<?> source, String... args) {
		nezhaApplicationAware = applicationAware;
		SpringApplicationBuilder builder = createSpringApplicationBuilder(source);
		builder.properties(String.format("spring.config.name=%s", NezhaConstant.NEZHA_FILE_NAME.replace(NezhaConstant.NEZHA_YML_FILE_EXT_NAME, "")));
		builder.properties(String.format("spring.cloud.bootstrap.name=%s", NezhaConstant.NEZHA_FILE_NAME.replace(NezhaConstant.NEZHA_YML_FILE_EXT_NAME, "")));
		builder.properties(String.format("spring.cloud.bootstrap.enabled=%s", cloudBootstrapEnabled));

		if (nezhaApplicationAware != null) {
			nezhaApplicationAware.configSpringBootBuilder(builder);
		}
		return builder.run(args);
	}

	public static ConfigurableApplicationContext run(NezhaApplicationAware applicationAware, Class<?> source, String... args) {
		return run(applicationAware, false, source, args);
	}

	public static ConfigurableApplicationContext run(Class<?> source, boolean cloudBootstrapEnabled, String... args) {
		return run(Nezha.defaultNezhaApplicationAware(), cloudBootstrapEnabled, source, args);
	}

	public static ConfigurableApplicationContext run(Class<?> source, String... args) {
		return run(Nezha.defaultNezhaApplicationAware(), source, args);
	}

	public static void initNezha(ConfigurableApplicationContext configurableApplicationContext) {
		if (INITIALIZED_NEZHA.compareAndSet(false, true)) {
			// init spring operator
			SpringUtil.setApplicationContext(configurableApplicationContext);
			// set nezhaProperties
			nezhaProperties = getBean(NezhaProperties.class);
			// show nezha information
			showInfo();
		}
	}

	/**
	 * resetSystemProperties
	 * appName, profile can't be reset,
	 *
	 * @param profile value
	 */
	public static void resetProperties(String profile) {
		if (NezhaApplication.HAD_RESET_SYSTEM_PROPERTIES.compareAndSet(false, true)) {
			Properties props = System.getProperties();
			if (nezhaApplicationAware != null) {
				nezhaApplicationAware.setSystemProperties(props);
			}

			if (nezhaApplicationAware != null) {
				String name = nezhaApplicationAware.getAppName(profile);
				props.setProperty("nezha.name", name);
				props.setProperty("spring.application.name", name);
				props.setProperty("project.name", name);
			}

			props.setProperty("nezha.env", profile);
			props.setProperty("nezha.version", Nezha.VERSION);
			props.setProperty("spring.profiles.active", profile);
			props.setProperty("seataEnv", profile);
			// make sure dubbo's logger use slf4j
			props.setProperty("dubbo.application.logger", "slf4j");

			// add dubbo debug local.property file
			if (profile.equals(Nezha.ProfileEnum.DEV.getValue())) {
				String relativelyPath = System.getProperty("user.dir");
				File file = new File(relativelyPath + File.separator + "dubbo-local.properties");
				if (file.exists()) {
					props.setProperty("dubbo.resolve.file", file.getPath());
					log.info("dubbo.resolve.file: [{}]", file.getPath());
				}
			}
		}
	}

	/**
	 * show nezha information
	 * nezha started show information to logs
	 */
	private static void showInfo() {
		String info = String.format("///nezha/// NezhaApplication[ %s ] is running", nezhaProperties.getAppName());
		if (log.isInfoEnabled()) {
			log.info(info);
		} else {
			System.out.println(info);
		}
	}

	/**
	 * get Bean
	 *
	 * @param clazz class
	 * @param <T>   T
	 * @return T object
	 */
	public static <T> T getBean(Class<T> clazz) {
		return SpringUtil.getBean(clazz);
	}

	/**
	 * get Bean
	 *
	 * @param beanId bean id
	 * @param <T>    T
	 * @return T object
	 */
	public static <T> T getBean(String beanId) {
		return SpringUtil.getBean(beanId);
	}

	/**
	 * get Bean
	 *
	 * @param beanName bean name
	 * @param clazz    class
	 * @param <T>      T
	 * @return T object
	 */
	public static <T> T getBean(String beanName, Class<T> clazz) {
		return SpringUtil.getBean(beanName, clazz);
	}

	/**
	 * send application event
	 *
	 * @param event event {@link ApplicationEvent}
	 */
	public static void publishEvent(ApplicationEvent event) {
		SpringUtil.publishEvent(event);
	}
}
