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

import com.thirtyai.nezha.common.exception.II18nException;
import com.thirtyai.nezha.common.i18n.I18n;
import com.thirtyai.nezha.core.Nezha;
import com.thirtyai.nezha.core.web.props.NezhaWebProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Default implementation of {@link ErrorAttributes}. Provides the following attributes
 * when possible:
 * status: 400,401,402,403,500,504; 200 (i18n is not null)
 * code: i18n is not null,then is i18n code; otherwise is http status code
 * desc: to code
 * data: null
 * reference DefaultErrorAttributes
 *
 * @author kyleju
 */
@Component
public class NezhaErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {

	public static final String ERROR_ATTRIBUTE = NezhaErrorAttributes.class.getName() + ".ERROR";
	public static final String TRACE_KEY = "trace";
	/**
	 * use for initRealErrorAndI18n
	 */
	public static final String I18N_ATTRIBUTE = NezhaErrorAttributes.class.getName() + ".I18N";
	public static final String REAL_ERROR_ATTRIBUTE = NezhaErrorAttributes.class.getName() + ".REAL.ERROR";
	/**
	 * end
	 */

	private final boolean includeException;

	private NezhaWebProperties nezhaWebProperties;

	public NezhaErrorAttributes() {
		this(false);
	}

	@Autowired
	public void setNezhaWebProperties(NezhaWebProperties nezhaWebProperties) {
		this.nezhaWebProperties = nezhaWebProperties;
	}

	/**
	 * Create a new {@link NezhaErrorAttributes} .
	 *
	 * @param includeException whether to include the "exception" attribute
	 */
	public NezhaErrorAttributes(boolean includeException) {
		this.includeException = includeException;
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		storeErrorAttributes(request, ex);
		return null;
	}

	private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
		/*
		search II18nException.
		 */
		if (ex != null) {
			if (ex instanceof II18nException) {
				request.setAttribute(I18N_ATTRIBUTE, ((II18nException) ex).getI18n());
			} else {
				while (ex.getCause() != null) {
					if (ex.getCause() instanceof II18nException) {
						request.setAttribute(I18N_ATTRIBUTE, ((II18nException) ex.getCause()).getI18n());
					}
					ex = (Exception) ex.getCause();
				}
			}
		}
		request.setAttribute(ERROR_ATTRIBUTE, ex);
	}

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		initRealErrorAndI18n(webRequest);
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("timestamp", new Date());
		addStatus(errorAttributes, webRequest);
		addErrorDetails(errorAttributes, webRequest, includeStackTrace);
		addPath(errorAttributes, webRequest);
		return addHtmlAttribute(errorAttributes, nezhaWebProperties);
	}

	/**
	 * init real error and nezha i18n
	 *
	 * @param webRequest web request
	 */
	private void initRealErrorAndI18n(WebRequest webRequest) {
		Throwable error = getError(webRequest);
		if (error != null) {
			// receive servlet Exception
			while (error instanceof ServletException && error.getCause() != null) {
				error = error.getCause();
			}
			webRequest.setAttribute(REAL_ERROR_ATTRIBUTE, error, RequestAttributes.SCOPE_REQUEST);
		}

		if (error != null) {
			BindingResult bindingResult = extractBindingResult(error);
			if (bindingResult != null && bindingResult.getErrorCount() > 0) {
				I18n i18n = I18n.valueOf(((MethodArgumentNotValidException) error).getBindingResult().getAllErrors().get(0).getDefaultMessage());
				if (i18n != null) {
					webRequest.setAttribute(I18N_ATTRIBUTE, i18n, RequestAttributes.SCOPE_REQUEST);
				}
			}
		}
	}


	private void addStatus(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
		if (requestAttributes.getAttribute(I18N_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) != null) {
			requestAttributes.setAttribute("javax.servlet.error.status_code", HttpStatus.OK.value(), RequestAttributes.SCOPE_REQUEST);
		}
		Integer status = getAttribute(requestAttributes, "javax.servlet.error.status_code");
		if (status == null) {
			errorAttributes.put("status", 999);
			errorAttributes.put("error", "None");
			return;
		}
		errorAttributes.put("status", status);
		try {
			errorAttributes.put("error", HttpStatus.valueOf(status).getReasonPhrase());
		} catch (Exception ex) {
			// Unable to obtain a reason
			errorAttributes.put("error", "Http Status " + status);
		}
	}

	private void addErrorDetails(Map<String, Object> errorAttributes, WebRequest webRequest,
								 boolean includeStackTrace) {
		if (webRequest.getAttribute(REAL_ERROR_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) != null) {
			if (webRequest.getAttribute(I18N_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) == null) {
				if (this.includeException) {
					errorAttributes.put("exception", webRequest.getAttribute(REAL_ERROR_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST).getClass().getName());
				}
				addErrorMessage(errorAttributes, (Throwable) webRequest.getAttribute(REAL_ERROR_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST));
				if (includeStackTrace) {
					addStackTrace(errorAttributes, (Throwable) webRequest.getAttribute(REAL_ERROR_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST));
				}
			}
		}
		if (webRequest.getAttribute(I18N_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) == null) {
			Object message = getAttribute(webRequest, "javax.servlet.error.message");
			boolean putMessage = (!StringUtils.isEmpty(message) || errorAttributes.get("message") == null) && !(webRequest.getAttribute(REAL_ERROR_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) instanceof BindingResult);
			if (putMessage) {
				errorAttributes.put("message", StringUtils.isEmpty(message) ? "No message available" : message);
			}
		}
	}

	private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
		BindingResult result = extractBindingResult(error);
		if (result == null) {
			errorAttributes.put("message", error.getMessage());
			return;
		}
		if (result.hasErrors()) {
			errorAttributes.put("errors", result.getAllErrors());
			errorAttributes.put("message", "Validation failed for object='" + result.getObjectName()
				+ "'. Error count: " + result.getErrorCount());
		} else {
			errorAttributes.put("message", "No errors");
		}
	}

	private BindingResult extractBindingResult(Throwable error) {
		if (error instanceof BindingResult) {
			return (BindingResult) error;
		}
		if (error instanceof MethodArgumentNotValidException) {
			return ((MethodArgumentNotValidException) error).getBindingResult();
		}
		return null;
	}

	private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
		StringWriter stackTrace = new StringWriter();
		error.printStackTrace(new PrintWriter(stackTrace));
		stackTrace.flush();
		errorAttributes.put("trace", stackTrace.toString());
	}

	private void addPath(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
		String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
		if (path != null) {
			errorAttributes.put("path", path);
		}
	}


	@Override
	public Throwable getError(WebRequest webRequest) {
		Throwable exception = getAttribute(webRequest, ERROR_ATTRIBUTE);
		if (exception == null) {
			exception = getAttribute(webRequest, "javax.servlet.error.exception");
		}
		return exception;
	}

	@SuppressWarnings("unchecked")
	private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * add html attribute
	 *
	 * @param errorAttributes    error attributes
	 * @param nezhaWebProperties nezha web properties
	 */
	public static Map<String, Object> addHtmlAttribute(Map<String, Object> errorAttributes, NezhaWebProperties nezhaWebProperties) {
		if (errorAttributes != null) {
			errorAttributes.put("homePageUrl", nezhaWebProperties.getHomePageUrl());
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
