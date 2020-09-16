/*
 * Copyright (c) kyle ju(nezha@thirtyai.com) All rights reserved.
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
package com.thirtyai.nezha.swagger.props;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * SwaggerProperties
 *
 * @author kyleju
 */
@Data
@Slf4j
@ConfigurationProperties("nezha.swagger")
public class SwaggerProperties {
	/**
	 * parse packages
	 **/
	private List<String> packages = new ArrayList<>();
	/**
	 * need parse paths
	 **/
	private List<String> paths = new ArrayList<>();
	/**
	 * exclude paths
	 **/
	private List<String> excludePaths = new ArrayList<>();
	/**
	 * description
	 **/
	private String title = "api center";
	/**
	 * 描述
	 **/
	private String description = "接口文档系统";
	/**
	 * version
	 **/
	private String version = "2.2.0";
	/**
	 * license
	 **/
	private String license = "Powered By nezha (哪吒)";
	/**
	 * license url
	 **/
	private String licenseUrl = "https://***";
	/**
	 * terms url
	 **/
	private String termsUrl = "https://***";

	/**
	 * host info
	 **/
	private String host = "";
	/**
	 * contact
	 */
	private Contact contact = new Contact();

	public SwaggerProperties() {
		log.info("///nezha/// swagger properties are loaded...");
	}

	@Data
	@NoArgsConstructor
	public static class Contact {

		/**
		 * name
		 **/
		private String name = "";
		/**
		 * url
		 **/
		private String url = "https://gitee.com/thirtyai";
		/**
		 * email
		 **/
		private String email = "";

	}
}
