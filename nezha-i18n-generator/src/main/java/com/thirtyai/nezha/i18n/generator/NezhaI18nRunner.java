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
package com.thirtyai.nezha.i18n.generator;

import com.thirtyai.nezha.core.NezhaApplication;
import com.thirtyai.nezha.i18n.I18nGenerator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author kyleju
 */
@SpringBootApplication
public class NezhaI18nRunner {

	private I18nGenerator i18nGenerator;

	public NezhaI18nRunner(ObjectProvider<I18nGenerator> i18nGeneratorsObjectProvider) {
		i18nGenerator = i18nGeneratorsObjectProvider.getIfAvailable();
	}

	public static void main(String[] args) {
		NezhaApplication.run(NezhaI18nRunner.class, args);
	}

	@Bean
	public ApplicationRunner runner() {
		return args -> {
			if (this.i18nGenerator != null) {
				i18nGenerator.doGenerate("nezha-i18n");
			}
		};
	}
}
