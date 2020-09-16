/*
 * Copyright (c) 2019-2020 ThirtyAi & kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * nezha application listener
 *
 * @author kyleju
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@Slf4j
public class NezhaApplicationWelcomeListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	private static final AtomicBoolean PROCESSED = new AtomicBoolean(false);

	public NezhaApplicationWelcomeListener() {
	}

	@Override
	public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
		// mark processed to be true
		if (PROCESSED.compareAndSet(false, true)) {
			this.printBanner(event.getSpringApplication(), event.getEnvironment());
		}
	}

	private void printBanner(SpringApplication application, ConfigurableEnvironment environment) {
		if (NezhaApplication.getBANNER_MODE() != Banner.Mode.OFF) {
			ResourceLoader resourceLoader = application.getResourceLoader();
			NezhaBannerPrinter bannerPrinter = new NezhaBannerPrinter(resourceLoader);
			if (NezhaApplication.getBANNER_MODE() == Banner.Mode.LOG) {
				bannerPrinter.print(environment, application.getMainApplicationClass(), log);
			}
			bannerPrinter.print(environment, application.getMainApplicationClass(), System.out);
		}
	}
}
