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
package com.thirtyai.nezha.web.security.props;

import com.thirtyai.nezha.common.NezhaConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kyleju
 */
@ConfigurationProperties("nezha.security")
@Data
public class NezhaSecurityProperties {
	/**
	 * 授权地址, 可以说登录地址
	 */
	private String authUrl = "/login";

	/**
	 * 是否支持 option method
	 */
	private boolean optionMethod = true;
	/**
	 * 参数token key
	 */
	private String paramTokenKey;
	/**
	 * header token key
	 */
	private String headerTokenKey = NezhaConstant.DEFAULT_AUTHORIZATION_HEADER_KEY;
	/**
	 * 忽略静态资源 resources/下的
	 */
	private List<String> staticExclusions = new ArrayList<>();
	/**
	 * 忽略url
	 */
	private List<String> urlExclusions = new ArrayList<>();

	/**
	 * JWT 配置
	 */
	private Jwt jwt = new Jwt();

	@Data
	public static class Jwt {
		private String header = "Authorization";
		private String base64Secret = "bmV6aGEtc3RhcnRlcnMtd2VsbGNvbWUtMTk4Ni0wcCUzQi8lMjhPTCUzRW5lemhhLXN0YXJ0ZXJzLXdlbGxjb21lLTE5ODYtMHAlM0IvJTI4T0wlM0VuZXpoYS1zdGFydGVycy13ZWxsY29tZS0xOTg2LTBwJTNCLyUyOE9MJTNFbmV6aGEtc3RhcnRlcnMtd2U=";
		private Long tokenValidityInSeconds = 60 * 60 * 24L;
		private Long tokenValidityInSecondsForRememberMe = 60 * 60 * 24 * 2L;
		private Long refreshTokenValidityInSeconds = 60 * 60 * 24 * 6L;
	}
}
