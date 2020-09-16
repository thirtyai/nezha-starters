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
package com.thirtyai.nezha.i18n;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.List;

/**
 * i18n generator properties
 *
 * @author kyleju
 */
@Data
@ConfigurationProperties("nezha.i18n")
public class I18nGeneratorProperties {
	List<I18nModule> lists;

	@Data
	public static class I18nModule {
		private String clsPackage;

		private String clsName = "AllStatus";

		private String resourceFolder = "status";

		private String defaultLanguage = "zh_CN";

		public String getFullResourceFolder() {
			return clsPackage.replace(StrUtil.DOT, File.separator) + File.separator + resourceFolder;
		}

		/**
		 * is status i18n
		 * todo: for improve I18n.valueOf(****) 's search range
		 */
		private boolean isStatus = true;
	}
}
