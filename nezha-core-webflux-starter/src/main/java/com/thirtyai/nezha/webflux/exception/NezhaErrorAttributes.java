/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.webflux.exception;

import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.common.exception.I18nException;
import com.thirtyai.nezha.common.exception.II18nException;
import com.thirtyai.nezha.core.Nezha;
import com.thirtyai.nezha.webflux.props.NezhaWebfluxProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class NezhaErrorAttributes implements ErrorAttributes, Ordered {

	public static final String ERROR_ATTRIBUTE = NezhaErrorAttributes.class.getName() + ".ERROR";

	private static final String TRACE_KEY = "trace";

	public static final String I18N_ATTRIBUTE = NezhaErrorAttributes.class.getName() + ".I18N";

	private final boolean includeException;

	private NezhaWebfluxProperties nezhaWebfluxProperties;

	@Autowired
	public void setNezhaWebProperties(NezhaWebfluxProperties nezhaWebfluxProperties) {
		this.nezhaWebfluxProperties = nezhaWebfluxProperties;
	}

	/**
	 * Create a new {@link NezhaErrorAttributes} instance that does not include the
	 * "exception" attribute.
	 */
	public NezhaErrorAttributes() {
		this(false);
	}

	/**
	 * Create a new {@link NezhaErrorAttributes} instance.
	 *
	 * @param includeException whether to include the "exception" attribute
	 */
	public NezhaErrorAttributes(boolean includeException) {
		this.includeException = includeException;
	}

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("timestamp", new Date());
		errorAttributes.put("path", request.path());
		Throwable error = getError(request);
		MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations
			.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
		HttpStatus errorStatus = determineHttpStatus(error, responseStatusAnnotation);
		errorAttributes.put("status", errorStatus.value());
		errorAttributes.put("error", errorStatus.getReasonPhrase());
		String message = determineMessage(error, responseStatusAnnotation);
		if (StrUtil.isBlank(message)) {
			message = errorStatus.getReasonPhrase();
		}
		errorAttributes.put("message", message);
		errorAttributes.put("requestId", request.exchange().getRequest().getId());
		handleException(errorAttributes, determineException(error), includeStackTrace);
		return addHtmlAttribute(errorAttributes, this.nezhaWebfluxProperties);
	}

	private HttpStatus determineHttpStatus(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
		if (error instanceof ResponseStatusException) {
			return ((ResponseStatusException) error).getStatus();
		}
		/*
		 * add i18n exception logic
		 */
		if (error instanceof I18nException) {
			return HttpStatus.OK;
		}
		return responseStatusAnnotation.getValue("code", HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String determineMessage(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
		if (error instanceof WebExchangeBindException) {
			return error.getMessage();
		}
		if (error instanceof ResponseStatusException) {
			return ((ResponseStatusException) error).getReason();
		}
		return responseStatusAnnotation.getValue("reason", String.class).orElseGet(error::getMessage);
	}

	private Throwable determineException(Throwable error) {
		if (error instanceof ResponseStatusException) {
			return (error.getCause() != null) ? error.getCause() : error;
		}
		return error;
	}

	private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
		StringWriter stackTrace = new StringWriter();
		error.printStackTrace(new PrintWriter(stackTrace));
		stackTrace.flush();
		errorAttributes.put(TRACE_KEY, stackTrace.toString());
	}

	private void handleException(Map<String, Object> errorAttributes, Throwable error, boolean includeStackTrace) {
		if (this.includeException) {
			errorAttributes.put("exception", error.getClass().getName());
		}
		if (includeStackTrace) {
			addStackTrace(errorAttributes, error);
		}
		if (error instanceof BindingResult) {
			BindingResult result = (BindingResult) error;
			if (result.hasErrors()) {
				errorAttributes.put("errors", result.getAllErrors());
			}
		}
	}

	@Override
	public Throwable getError(ServerRequest request) {
		return (Throwable) request.attribute(ERROR_ATTRIBUTE)
			.orElseThrow(() -> new IllegalStateException("Missing exception attribute in ServerWebExchange"));
	}

	@Override
	public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
		/*
		 * add i18n exception logic
		 */
		if (error != null) {
			if (error instanceof I18nException) {
				exchange.getAttributes().putIfAbsent(I18N_ATTRIBUTE, error);
			} else {
				while (error.getCause() != null) {
					if (error.getCause() instanceof II18nException) {
						exchange.getAttributes().putIfAbsent(I18N_ATTRIBUTE, ((II18nException) error.getCause()).getI18n());
					}
					error = error.getCause();
				}
			}
		}
		exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE, error);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	/**
	 * add html attribute
	 *
	 * @param errorAttributes        error attributes
	 * @param nezhaWebfluxProperties nezha web flux properties
	 */
	public static Map<String, Object> addHtmlAttribute(Map<String, Object> errorAttributes, NezhaWebfluxProperties nezhaWebfluxProperties) {
		if (errorAttributes != null) {
			errorAttributes.put("homePageUrl", nezhaWebfluxProperties.getHomePageUrl());
			errorAttributes.put("springVersion", SpringBootVersion.getVersion());
			errorAttributes.put("nezhaVersion", Nezha.VERSION);
			if (!errorAttributes.containsKey(TRACE_KEY)) {
				errorAttributes.put(TRACE_KEY, "");
			}
			errorAttributes.put("webSite", Nezha.WEB_SITE);
		}
		return errorAttributes;
	}

	/**
	 * remove html attribute
	 *
	 * @param errorAttributes error attributes
	 */
	public static Map<String, Object> removeHtmlAttribute(Map<String, Object> errorAttributes) {
		if (errorAttributes != null) {
			errorAttributes.remove("homePageUrl");
			errorAttributes.remove("springVersion");
			errorAttributes.remove("nezhaVersion");
			errorAttributes.remove("webSite");
		}
		return errorAttributes;
	}
}
