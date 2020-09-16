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

import com.thirtyai.nezha.core.props.NezhaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * nezha application run listener
 *
 * @author kyleju
 */
@Slf4j
public class NezhaApplicationRunListener implements SpringApplicationRunListener, Ordered {

	public NezhaApplicationRunListener(SpringApplication application, String[] args) {
	}

	@Override
	public void starting() {
	}

	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
		String[] activeProfiles = environment.getActiveProfiles();
		List<String> profiles = Arrays.asList(activeProfiles);
		List<String> presetProfiles = new ArrayList<>(Arrays.asList(Nezha.ProfileEnum.DEV.getValue(), Nezha.ProfileEnum.TEST.getValue(), Nezha.ProfileEnum.PROD.getValue()));
		presetProfiles.retainAll(profiles);

		// dev、test、prod
		String profile;
		if (profiles.isEmpty()) {
			profiles = new ArrayList<>();
			profile = Nezha.ProfileEnum.DEV.getValue();
			profiles.add(profile);
		} else if (profiles.size() == 1) {
			profile = profiles.get(0);
			if (!presetProfiles.contains(profile)) {
				throw new RuntimeException("profile must be :[" + StringUtils.arrayToCommaDelimitedString(presetProfiles.toArray()) + "]");
			}
		} else {
			throw new RuntimeException("environment variables exist many profile:[" + StringUtils.arrayToCommaDelimitedString(activeProfiles) + "]");
		}

		String currentJarPath = NezhaApplication.class.getResource("/").getPath().split("!")[0];
		NezhaApplication.resetProperties(profile);
		log.info(String.format("nezha is starting，the profile environment is:[%s]，current jar path:[%s] ", String.join(",", profiles), currentJarPath));
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {

	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {

	}

	@Override
	public void started(ConfigurableApplicationContext context) {
		/*
		 * init nezha
		 */
		context.getBeanProvider(NezhaProperties.class).ifAvailable(bean -> NezhaApplication.initNezha(context));
	}

	@Override
	public void running(ConfigurableApplicationContext context) {

	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
