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
package com.thirtyai.nezha.core;

/**
 * nezha
 *
 * @author kyleju
 */
public class Nezha {
	public final static String GROUP_ID = "com.thirtyai";
	public final static String ARTIFACT_ID = "nezha-bom-starter";
	public final static String WEB_SITE = "https://nezha.thirtyai.com";
	public final static String VERSION = "0.4.9-202x207-SNAPSHOT";

	/**
	 * profile enum ( dev,test,prod )
	 */
	public enum ProfileEnum {
		/**
		 * test
		 */
		TEST("test"),
		/**
		 * development
		 */
		DEV("dev"),
		/**
		 * product
		 */
		PROD("prod");

		private final String val;

		public String getValue() {
			return this.val;
		}

		ProfileEnum(String value) {
			this.val = value;
		}
	}

	/**
	 * default NezhaApplicationAware
	 *
	 * @return default nezha application aware {@link NezhaApplicationAware}
	 */
	public static NezhaApplicationAware defaultNezhaApplicationAware() {
		return new NezhaApplicationAware() {
		};
	}
}
