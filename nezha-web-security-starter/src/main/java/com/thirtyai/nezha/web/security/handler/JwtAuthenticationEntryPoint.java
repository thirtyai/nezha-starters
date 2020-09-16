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
package com.thirtyai.nezha.web.security.handler;

import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.i18n.Status;
import com.thirtyai.nezha.common.util.JsonUtil;
import com.thirtyai.nezha.common.wrap.Resp;
import com.thirtyai.nezha.common.wrap.RespBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * authentication (not login)
 *
 * @author kyleju
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(javax.servlet.http.HttpServletRequest httpServletRequest, javax.servlet.http.HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
		if (httpServletRequest.getContentType() == null || httpServletRequest.getContentType().equals(MediaType.TEXT_HTML_VALUE)) {
			httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} else {
			Resp<String> resp = RespBuilder.build(HttpStatus.OK.value(), Status.HTTP_Un_Authentication, StrUtil.EMPTY, httpServletRequest.getLocale().toString());
			httpServletResponse.getWriter().write(Objects.requireNonNull(JsonUtil.toJson(resp)));
		}
	}
}
