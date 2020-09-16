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
package com.thirtyai.nezha.core.web.exception;

import com.thirtyai.nezha.core.web.utils.RequestUtil;
import com.thirtyai.nezha.i18n.Status;
import com.thirtyai.nezha.common.i18n.I18n;
import com.thirtyai.nezha.common.wrap.Resp;
import com.thirtyai.nezha.common.wrap.RespBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * nezha error controller
 *
 * @author kyleju
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class NezhaErrorController extends AbstractErrorController {

	private final ErrorProperties errorProperties;

	/**
	 * Create a new {@link NezhaErrorController} instance.
	 *
	 * @param errorAttributes the error attributes
	 * @param errorProperties configuration properties
	 */
	public NezhaErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
		this(errorAttributes, errorProperties, Collections.emptyList());
	}

	/**
	 * Create a new {@link BasicErrorController} instance.
	 *
	 * @param errorAttributes    the error attributes
	 * @param errorProperties    configuration properties
	 * @param errorViewResolvers error view resolvers
	 */
	public NezhaErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
								List<ErrorViewResolver> errorViewResolvers) {
		super(errorAttributes, errorViewResolvers);
		Assert.notNull(errorProperties, "ErrorProperties must not be null");
		this.errorProperties = errorProperties;
	}


	@Override
	public String getErrorPath() {
		return this.errorProperties.getPath();
	}


	/**
	 * must using this construction.
	 *
	 * @param errorAttributes    error attributes
	 * @param serverProperties   server properties
	 * @param errorViewResolvers error view resolvers
	 */
	@Autowired
	public NezhaErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties, List<ErrorViewResolver> errorViewResolvers) {
		this(errorAttributes, serverProperties.getError(), errorViewResolvers);
	}

	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		HttpStatus status = getStatus(request);
		Map<String, Object> model = Collections
			.unmodifiableMap(getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
		response.setStatus(status.value());
		ModelAndView modelAndView = resolveErrorView(request, response, status, model);
		return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
	}

	@RequestMapping
	public ResponseEntity<Resp<Object>> error(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		if (status == HttpStatus.NO_CONTENT) {
			return new ResponseEntity<>(RespBuilder.build(HttpStatus.OK.value(), Status.HTTP_Forbidden, null, request.getLocale().toString()), status);
		}
		Map<String, Object> body = NezhaErrorAttributes.removeHtmlAttribute(getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL)));
		Object i18n = request.getAttribute(NezhaErrorAttributes.I18N_ATTRIBUTE);
		if (i18n instanceof I18n) {
			return new ResponseEntity<>(RespBuilder.build(HttpStatus.OK.value(), (I18n) i18n, null, RequestUtil.getTotal(request), RequestUtil.getLocal(request)), status);
		}
		Object bodyMessage = body.get("message");
		return new ResponseEntity<>(RespBuilder.build(HttpStatus.OK.value(), status.value() + "", bodyMessage == null ? status.getReasonPhrase() : bodyMessage.toString(), null, RequestUtil.getTotal(request)), status);
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public ResponseEntity<String> mediaTypeNotAcceptable(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		return ResponseEntity.status(status).build();
	}

	/**
	 * Determine if the stacktrace attribute should be included.
	 *
	 * @param request  the source request
	 * @param produces the media type produced (or {@code MediaType.ALL})
	 * @return if the stacktrace attribute should be included
	 */
	protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
		ErrorProperties.IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
		if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
			return true;
		}
		if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
			return getTraceParameter(request);
		}
		return false;
	}

	/**
	 * Provide access to the error properties.
	 *
	 * @return the error properties
	 */
	protected ErrorProperties getErrorProperties() {
		return this.errorProperties;
	}

}
