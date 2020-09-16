/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.web.security.jwt;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * jwt token extend interface
 *
 * @author kyleju
 */
public interface IJwtTokenSafeExtender<T> extends IAuthorities {

	/**
	 * get distributed token status code, the code is for making distributed jwt token expire , when the user information had changed.
	 *
	 * @param subjectValue string
	 * @return code suggest md5(some key information, example: update time or other key information ....)
	 */
	String getTokenDistributionStatusCode(String subjectValue);

	/**
	 * get subject
	 *
	 * @param principal principal
	 * @return subject
	 */
	default String getSubject(Object principal) {
		if (principal instanceof JwtUser) {
			return Convert.toStr(((JwtUser) principal).getId());
		}

		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}
		return principal.toString();
	}

	/**
	 * get JwtUser make jwt token almost is short.
	 *
	 * @param subjectValue         subject value
	 * @param claimsAuthorityValue claimsAuthorityValue
	 * @return value {@link JwtUser}
	 */
	JwtUser<T> getJwtUser(String subjectValue, String claimsAuthorityValue);

	/**
	 * encode token
	 *
	 * @param token raw token
	 * @return value
	 */
	default String encode(String token) {
		return token;
	}

	/**
	 * decode token
	 *
	 * @param encodedToken encoded token
	 * @return raw token
	 */
	default String decode(String encodedToken) {
		return encodedToken;
	}

	/**
	 * jwt provider will invoke
	 */
	default void clean() {
	}
}
