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

import cn.hutool.core.convert.Convert;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.common.i18n.I18n;
import com.thirtyai.nezha.common.wrap.RespBuilder;
import com.thirtyai.nezha.i18n.Status;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * nezha error web exception handler
 *
 * @author kyleju
 */
public class NezhaErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

	private static final MediaType TEXT_HTML_UTF8 = new MediaType("text", "html", StandardCharsets.UTF_8);

	private static final Map<HttpStatus.Series, String> SERIES_VIEWS;

	private final ApplicationContext applicationContext;
	private final ResourceProperties resourceProperties;
	private final TemplateAvailabilityProviders templateAvailabilityProviders;

	static {
		Map<HttpStatus.Series, String> views = new EnumMap<>(HttpStatus.Series.class);
		views.put(HttpStatus.Series.CLIENT_ERROR, "4xx");
		views.put(HttpStatus.Series.SERVER_ERROR, "5xx");
		SERIES_VIEWS = Collections.unmodifiableMap(views);
	}

	private final ErrorProperties errorProperties;

	/**
	 * Create a new {@code DefaultErrorWebExceptionHandler} instance.
	 *
	 * @param errorAttributes    the error attributes
	 * @param resourceProperties the resources configuration properties
	 * @param errorProperties    the error configuration properties
	 * @param applicationContext the current application context
	 */
	public NezhaErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
										 ErrorProperties errorProperties, ApplicationContext applicationContext) {
		super(errorAttributes, resourceProperties, applicationContext);
		this.errorProperties = errorProperties;
		this.applicationContext = applicationContext;
		this.resourceProperties = resourceProperties;

		this.templateAvailabilityProviders = new TemplateAvailabilityProviders(applicationContext);
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(acceptsTextHtml(), this::renderErrorView).andRoute(RequestPredicates.all(), this::renderErrorResponse);
	}

	/**
	 * Render the error information as an HTML view.
	 *
	 * @param request the current request
	 * @return a {@code Publisher} of the HTTP response
	 */
	protected Mono<ServerResponse> renderErrorView(ServerRequest request) {
		boolean includeStackTrace = isIncludeStackTrace(request, MediaType.TEXT_HTML);
		Map<String, Object> error = getErrorAttributes(request, includeStackTrace);
		int errorStatus = getHttpStatus(error);
		ServerResponse.BodyBuilder responseBody = ServerResponse.status(errorStatus).contentType(TEXT_HTML_UTF8);
		return Flux.just(getData(errorStatus).toArray(new String[]{}))
			.flatMap((viewName) -> renderErrorView(viewName, responseBody, error))
			.switchIfEmpty(this.errorProperties.getWhitelabel().isEnabled()
				? renderDefaultErrorView(responseBody, error) : Mono.error(getError(request)))
			.next();
	}

	/**
	 * Render the given error data as a view, using a template view if available or a
	 * static HTML file if available otherwise. This will return an empty
	 * {@code Publisher} if none of the above are available.
	 *
	 * @param viewName     the view name
	 * @param responseBody the error response being built
	 * @param error        the error data as a map
	 * @return a Publisher of the {@link ServerResponse}
	 */
	@Override
	protected Mono<ServerResponse> renderErrorView(String viewName, ServerResponse.BodyBuilder responseBody,
												   Map<String, Object> error) {
		if (isTemplateAvailable(viewName)) {
			return responseBody.render(viewName, error);
		}
		Resource resource = resolveResource(viewName);
		if (resource != null) {
			try {
				AtomicReference<String> result = new AtomicReference<>(new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)).lines().parallel().collect(Collectors.joining(System.lineSeparator())));
				if (error != null) {
					error.forEach((key, item) -> {
						result.set(result.get().replace(String.format("${%s}", key), item == null ? "" : item.toString()));
					});
				}
				return responseBody.bodyValue(result.get());
			} catch (IOException e) {
				// igonore
			}
		}
		return Mono.empty();
	}

	private Resource resolveResource(String viewName) {
		for (String location : this.resourceProperties.getStaticLocations()) {
			try {
				Resource resource = this.applicationContext.getResource(location);
				resource = resource.createRelative(viewName + ".html");
				if (resource.exists()) {
					return resource;
				}
			} catch (Exception ex) {
				// Ignore
			}
		}
		return null;
	}

	private boolean isTemplateAvailable(String viewName) {
		return this.templateAvailabilityProviders.getProvider(viewName, this.applicationContext) != null;
	}


	private List<String> getData(int errorStatus) {
		List<String> data = new ArrayList<>();
		data.add("error/" + errorStatus);
		HttpStatus.Series series = HttpStatus.Series.resolve(errorStatus);
		if (series != null) {
			data.add("error/" + SERIES_VIEWS.get(series));
		}
		data.add("error/error");
		return data;
	}

	/**
	 * Render the error information as a JSON payload.
	 *
	 * @param request the current request
	 * @return a {@code Publisher} of the HTTP response
	 */
	protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		boolean includeStackTrace = isIncludeStackTrace(request, MediaType.ALL);
		Map<String, Object> error = getErrorAttributes(request, includeStackTrace);

		HttpStatus status = HttpStatus.valueOf(Convert.toInt(error.get("status")));
		I18n i18n = null;
		if (status == HttpStatus.NO_CONTENT) {
			status = HttpStatus.OK;
			i18n = Status.HTTP_Forbidden;
		} else {
			Optional<Object> i18nObj = request.attribute(NezhaErrorAttributes.I18N_ATTRIBUTE);
			if (i18nObj.isPresent()) {
				if (i18nObj.get() instanceof I18n) {
					i18n = (I18n) i18nObj.get();
				}
			}
		}
		if (i18n != null) {
			return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(RespBuilder.build(HttpStatus.OK.value(), i18n, null, NezhaConstant.DEFAULT_VALUE, LocaleContextHolder.getLocale().toString())));
		}
		Map<String, Object> body = NezhaErrorAttributes.removeHtmlAttribute(getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL)));
		Object bodyMessage = body.get("message");
		return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(RespBuilder.build(HttpStatus.OK.value(), status.value() + "", bodyMessage == null ? status.getReasonPhrase() : bodyMessage.toString(), null, NezhaConstant.DEFAULT_VALUE)));
	}

	/**
	 * Determine if the stacktrace attribute should be included.
	 *
	 * @param request  the source request
	 * @param produces the media type produced (or {@code MediaType.ALL})
	 * @return if the stacktrace attribute should be included
	 */
	protected boolean isIncludeStackTrace(ServerRequest request, MediaType produces) {
		ErrorProperties.IncludeStacktrace include = this.errorProperties.getIncludeStacktrace();
		if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
			return true;
		}
		if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
			return isTraceEnabled(request);
		}
		return false;
	}

	/**
	 * Get the HTTP error status information from the error map.
	 *
	 * @param errorAttributes the current error information
	 * @return the error HTTP status
	 */
	protected int getHttpStatus(Map<String, Object> errorAttributes) {
		return (int) errorAttributes.get("status");
	}

	/**
	 * Predicate that checks whether the current request explicitly support
	 * {@code "text/html"} media type.
	 * <p>
	 * The "match-all" media type is not considered here.
	 *
	 * @return the request predicate
	 */
	protected RequestPredicate acceptsTextHtml() {
		return (serverRequest) -> {
			try {
				List<MediaType> acceptedMediaTypes = serverRequest.headers().accept();
				acceptedMediaTypes.remove(MediaType.ALL);
				MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
				return acceptedMediaTypes.stream().anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
			} catch (InvalidMediaTypeException ex) {
				return false;
			}
		};
	}
}
