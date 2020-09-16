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
package com.thirtyai.nezha.core.web.props;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

/**
 * nezha web properties
 *
 * @author kyleju
 */
@ConfigurationProperties("nezha.web")
@Slf4j
@Data
public class NezhaWebProperties {

	/**
	 * upload folders
	 */
	private List<UploadFolderConfig> uploadFolders = new ArrayList<>();
	/**
	 * static exclusion
	 */
	private List<String> staticExclusions = new ArrayList<>();

	/**
	 * check auth key
	 */
	private String authKey;

	/**
	 * home page url
	 */
	private String homePageUrl = "#";

	private int filterOrder = Ordered.HIGHEST_PRECEDENCE;

	public NezhaWebProperties() {
		log.info("///nezha/// web core properties are loaded...");
	}

	@Data
	public static class UploadFolderConfig {
		private String name;
		/**
		 * real folder
		 */
		private String realFolder = System.getProperty("user.dir");
		/**
		 * content folder
		 */
		private String contentFolder = "/";

		/**
		 * compress
		 */
		private boolean compress;
	}

}
