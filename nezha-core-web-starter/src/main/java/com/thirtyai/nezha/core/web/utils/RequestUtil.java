package com.thirtyai.nezha.core.web.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.common.wrap.RespBuilder;
import com.thirtyai.nezha.i18n.Status;
import com.thirtyai.nezha.common.i18n.I18n;
import com.thirtyai.nezha.common.wrap.Resp;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * request util
 *
 * @author kyleju
 */
public class RequestUtil {

	private static final String ATTRIBUTE_RESPONSE_BODY_I18N_KEY = "nezha_attribute_response_i18n_key";
	private static final String ATTRIBUTE_RESPONSE_BODY_LIST_TOTAL_KEY = "nezha_attribute_response_list_total_key";

	static String unKnown = "unknown";

	static String[] mobileAgents = {"iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
		"opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod", "nokia",
		"samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma", "docomo",
		"up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos", "techfaith",
		"palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem", "wellcom", "bunjalloo",
		"maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos", "pantech", "gionee", "portalmmm",
		"jig browser", "hiptop", "benq", "haier", "^lct", "320x320", "240x320", "176x220", "w3c ", "acs-", "alav",
		"alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang",
		"doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g",
		"lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki",
		"oper", "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams", "sany", "sch-",
		"sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo",
		"teli", "tim-", "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc",
		"winw", "winw", "xda", "xda-", "googlebot-mobile"};

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equalsIgnoreCase(header);
	}

	public static boolean isMultipartRequest(HttpServletRequest request) {
		String contentType = request.getContentType();
		return contentType != null && contentType.toLowerCase().indexOf("multipart") != -1;
	}

	/**
	 * is mobile browser
	 *
	 * @return
	 */
	public static boolean isMobileBrowser(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		if (ua == null) {
			return false;
		}
		ua = ua.toLowerCase();
		for (String mobileAgent : mobileAgents) {
			if (ua.indexOf(mobileAgent) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * is wechat browser
	 *
	 * @return value
	 */
	public static boolean isWechatBrowser(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		if (ua == null) {
			return false;
		}
		ua = ua.toLowerCase();
		if (ua.indexOf("micromessenger") > 0) {
			return true;
		}
		return false;
	}


	/**
	 * is wechat pc browser
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @return value
	 */
	public static boolean isWechatPcBrowser(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		if (ua == null) {
			return false;
		}
		ua = ua.toLowerCase();
		if (ua.indexOf("windowswechat") > 0) {
			return true;
		}
		return false;
	}

	/**
	 * is IE browser
	 *
	 * @return
	 */
	public static boolean isIEBrowser(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		if (ua == null) {
			return false;
		}

		ua = ua.toLowerCase();
		if (ua.indexOf("msie") > 0) {
			return true;
		}

		if (ua.indexOf("gecko") > 0 && ua.indexOf("rv:11") > 0) {
			return true;
		}
		return false;
	}

	public static String getIpAddress(HttpServletRequest request) {

		String ip = request.getHeader("X-requested-For");
		if (StrUtil.isBlank(ip) || unKnown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (StrUtil.isBlank(ip) || unKnown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StrUtil.isBlank(ip) || unKnown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StrUtil.isBlank(ip) || unKnown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StrUtil.isBlank(ip) || unKnown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StrUtil.isBlank(ip) || unKnown.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip != null && ip.contains(StrUtil.COMMA)) {
			String[] ips = ip.split(StrUtil.COMMA);
			for (String strIp : ips) {
				if (!(unKnown.equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}

		return ip;
	}

	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}


	public static String getReferer(HttpServletRequest request) {
		return request.getHeader("Referer");
	}


	/**
	 * Stores an attribute in this request
	 *
	 * @param name    a String specifying the name of the attribute
	 * @param value   the Object to be stored
	 * @param request request {@link HttpServletRequest}
	 */
	public static void setAttr(String name, Object value, HttpServletRequest request) {
		request.setAttribute(name, value);
	}

	/**
	 * Removes an attribute from this request
	 *
	 * @param name    a String specifying the name of the attribute to remove
	 * @param request request {@link HttpServletRequest}
	 */
	public static void removeAttr(String name, HttpServletRequest request) {
		request.removeAttribute(name);
	}

	/**
	 * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
	 *
	 * @param attrMap key and value as attribute of the map to be stored
	 * @param request request {@link HttpServletRequest}
	 */
	public static void setAttrs(Map<String, Object> attrMap, HttpServletRequest request) {
		for (Map.Entry<String, Object> entry : attrMap.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Returns the value of a request parameter as a String, or null if the parameter does not exist.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return a String representing the single value of the parameter
	 */
	public static String getPara(String name, HttpServletRequest request) {
		return request.getParameter(name);
	}

	/**
	 * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
	 *
	 * @param name         a String specifying the name of the parameter
	 * @param defaultValue a String value be returned when the value of parameter is null
	 * @param request request {@link HttpServletRequest}
	 * @return a String representing the single value of the parameter
	 */
	public static String getPara(String name, String defaultValue, HttpServletRequest request) {
		String result = request.getParameter(name);
		return result != null && !"".equals(result) ? result : defaultValue;
	}

	/**
	 * Returns the values of the request parameters as a Map.
	 *
	 * @param request request {@link HttpServletRequest}
	 * @return a Map contains all the parameters name and value
	 */
	public static Map<String, String[]> getParaMap(HttpServletRequest request) {
		return request.getParameterMap();
	}

	/**
	 * Returns an Enumeration of String objects containing the names of the parameters
	 * contained in this request. If the request has no parameters, the method returns
	 * an empty Enumeration.
	 *
	 * @param request request {@link HttpServletRequest}
	 * @return an Enumeration of String objects, each String containing the name of
	 * a request parameter; or an empty Enumeration if the request has no parameters
	 */
	public static Enumeration<String> getParaNames(HttpServletRequest request) {
		return request.getParameterNames();
	}

	/**
	 * Returns an array of String objects containing all of the values the given request
	 * parameter has, or null if the parameter does not exist. If the parameter has a
	 * single value, the array has a length of 1.
	 *
	 * @param name    a String containing the name of the parameter whose value is requested
	 * @param request request {@link HttpServletRequest}
	 * @return an array of String objects containing the parameter's values
	 */
	public static String[] getParaValues(String name, HttpServletRequest request) {
		return request.getParameterValues(name);
	}

	/**
	 * Returns an array of Integer objects containing all of the values the given request
	 * parameter has, or null if the parameter does not exist. If the parameter has a
	 * single value, the array has a length of 1.
	 *
	 * @param name    a String containing the name of the parameter whose value is requested
	 * @param request request {@link HttpServletRequest}
	 * @return an array of Integer objects containing the parameter's values
	 */
	public static Integer[] getParaValuesToInt(String name, HttpServletRequest request) {
		String[] values = request.getParameterValues(name);
		if (values == null || values.length == 0) {
			return null;
		}
		Integer[] result = new Integer[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = StrUtil.isBlank(values[i]) ? null : Integer.parseInt(values[i]);
		}
		return result;
	}

	public static Long[] getParaValuesToLong(String name, HttpServletRequest request) {
		String[] values = request.getParameterValues(name);
		if (values == null || values.length == 0) {
			return null;
		}
		Long[] result = new Long[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = StrUtil.isBlank(values[i]) ? null : Long.parseLong(values[i]);
		}
		return result;
	}

	/**
	 * Returns an Enumeration containing the names of the attributes available to this request.
	 * This method returns an empty Enumeration if the request has no attributes available to it.
	 *
	 * @param request request {@link HttpServletRequest}
	 * @return an Enumeration of strings containing the names of the request's attributes
	 */
	public static Enumeration<String> getAttrNames(HttpServletRequest request) {
		return request.getAttributeNames();
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 *
	 * @param name    a String specifying the name of the attribute
	 * @param request request {@link HttpServletRequest}
	 * @return an Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public static <T> T getAttr(String name, HttpServletRequest request) {
		return (T) request.getAttribute(name);
	}

	public static <T> T getAttr(String name, T defaultValue, HttpServletRequest request) {
		T result = (T) request.getAttribute(name);
		return result != null ? result : defaultValue;
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 *
	 * @param name    a String specifying the name of the attribute
	 * @param request request {@link HttpServletRequest}
	 * @return an String Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public static String getAttrForStr(String name, HttpServletRequest request) {
		return (String) request.getAttribute(name);
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 *
	 * @param name    a String specifying the name of the attribute
	 * @param request request {@link HttpServletRequest}
	 * @return an Integer Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public static Integer getAttrForInt(String name, HttpServletRequest request) {
		return (Integer) request.getAttribute(name);
	}

	/**
	 * Returns the value of the specified request header as a String.
	 */
	public static String getHeader(String name, HttpServletRequest request) {
		return request.getHeader(name);
	}

	private static Integer toInt(String value, Integer defaultValue) {
		try {
			if (StrUtil.isBlank(value)) {
				return defaultValue;
			}
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n")) {
				return -Integer.parseInt(value.substring(1));
			}
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
			//throw new ActionException(400, renderManager.getRenderFactory().getErrorRender(400),  "Can not parse the parameter \"" + value + "\" to Integer value.");
		}
	}

	/**
	 * Returns the value of a request parameter and convert to Integer.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return a Integer representing the single value of the parameter
	 */
	public static Integer getParaToInt(String name, HttpServletRequest request) {
		return toInt(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Integer with a default value if it is null.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return a Integer representing the single value of the parameter
	 */
	public static Integer getParaToInt(String name, Integer defaultValue, HttpServletRequest request) {
		return toInt(request.getParameter(name), defaultValue);
	}

	private static Long toLong(String value, Long defaultValue) {
		try {
			if (StrUtil.isBlank(value)) {
				return defaultValue;
			}
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n")) {
				return -Long.parseLong(value.substring(1));
			}
			return Long.parseLong(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Returns the value of a request parameter and convert to Long.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return a Integer representing the single value of the parameter
	 */
	public static Long getParaToLong(String name, HttpServletRequest request) {
		return toLong(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Long with a default value if it is null.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return a Integer representing the single value of the parameter
	 */
	public static Long getParaToLong(String name, Long defaultValue, HttpServletRequest request) {
		return toLong(request.getParameter(name), defaultValue);
	}

	private static Boolean toBoolean(String value, Boolean defaultValue) {
		if (StrUtil.isBlank(value)) {
			return defaultValue;
		}
		value = value.trim().toLowerCase();
		if ("1".equals(value) || "true".equals(value)) {
			return Boolean.TRUE;
		} else if ("0".equals(value) || "false".equals(value)) {
			return Boolean.FALSE;
		}
		return false;
		//throw new ActionException(400, renderManager.getRenderFactory().getErrorRender(400), "Can not parse the parameter \"" + value + "\" to Boolean value.");
	}

	/**
	 * Returns the value of a request parameter and convert to Boolean.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", null if parameter is not exists
	 */
	public static Boolean getParaToBoolean(String name, HttpServletRequest request) {
		return toBoolean(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", default value if it is null
	 */
	public static Boolean getParaToBoolean(String name, Boolean defaultValue, HttpServletRequest request) {
		return toBoolean(request.getParameter(name), defaultValue);
	}

	private static Date toDate(String value, Date defaultValue) {
		try {
			if (StrUtil.isBlank(value)) {
				return defaultValue;
			}
			return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(value.trim());
		} catch (Exception e) {
			return null;
			// throw new ActionException(400, renderManager.getRenderFactory().getErrorRender(400),  "Can not parse the parameter \"" + value + "\" to Date value.");
		}
	}

	/**
	 * Returns the value of a request parameter and convert to Date.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return a Date representing the single value of the parameter
	 */
	public static Date getParaToDate(String name, HttpServletRequest request) {
		return toDate(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Date with a default value if it is null.
	 *
	 * @param name    a String specifying the name of the parameter
	 * @param request request {@link HttpServletRequest}
	 * @return a Date representing the single value of the parameter
	 */
	public static Date getParaToDate(String name, Date defaultValue, HttpServletRequest request) {
		return toDate(request.getParameter(name), defaultValue);
	}

	/**
	 * Return HttpSession.
	 *
	 * @param request request {@link HttpServletRequest}
	 */
	public static HttpSession getSession(HttpServletRequest request) {
		return request.getSession();
	}

	/**
	 * Return HttpSession.
	 *
	 * @param create  a boolean specifying create HttpSession if it not exists
	 * @param request request {@link HttpServletRequest}
	 */
	public static HttpSession getSession(boolean create, HttpServletRequest request) {
		return request.getSession(create);
	}

	/**
	 * Return a Object from session.
	 *
	 * @param key     a String specifying the key of the Object stored in session
	 * @param request request {@link HttpServletRequest}
	 */
	public static <T> T getSessionAttr(String key, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session != null ? (T) session.getAttribute(key) : null;
	}

	public static <T> T getSessionAttr(String key, T defaultValue, HttpServletRequest request) {
		T result = getSessionAttr(key, request);
		return result != null ? result : defaultValue;
	}

	/**
	 * Store Object to session.
	 *
	 * @param key     a String specifying the key of the Object stored in session
	 * @param value   a Object specifying the value stored in session
	 * @param request request {@link HttpServletRequest}
	 */
	public static void setSessionAttr(String key, Object value, HttpServletRequest request) {
		request.getSession(true).setAttribute(key, value);
	}

	/**
	 * Remove Object in session.
	 *
	 * @param key a String specifying the key of the Object stored in session
	 */
	public static void removeSessionAttr(String key, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(key);
		}
	}

	/**
	 * Get cookie value by cookie name.
	 */
	public static String getCookie(String name, String defaultValue, HttpServletRequest request) {
		Cookie cookie = getCookieObject(name, request);
		return cookie != null ? cookie.getValue() : defaultValue;
	}

	/**
	 * Get cookie value by cookie name.
	 */
	public static String getCookie(String name, HttpServletRequest request) {
		return getCookie(name, null, request);
	}

	/**
	 * Get cookie value by cookie name and convert to Integer.
	 */
	public static Integer getCookieToInt(String name, HttpServletRequest request) {
		String result = getCookie(name, request);
		return result != null ? Integer.parseInt(result) : null;
	}

	/**
	 * Get cookie value by cookie name and convert to Integer.
	 */
	public static Integer getCookieToInt(String name, Integer defaultValue, HttpServletRequest request) {
		String result = getCookie(name, request);
		return result != null ? Integer.parseInt(result) : defaultValue;
	}

	/**
	 * Get cookie value by cookie name and convert to Long.
	 */
	public static Long getCookieToLong(String name, HttpServletRequest request) {
		String result = getCookie(name, request);
		return result != null ? Long.parseLong(result) : null;
	}

	/**
	 * Get cookie value by cookie name and convert to Long.
	 */
	public static Long getCookieToLong(String name, Long defaultValue, HttpServletRequest request) {
		String result = getCookie(name, request);
		return result != null ? Long.parseLong(result) : defaultValue;
	}

	/**
	 * Get cookie object by cookie name.
	 */
	public static Cookie getCookieObject(String name, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * Get all cookie objects.
	 *
	 * @param request request {@link HttpServletRequest}
	 */
	public static Cookie[] getCookieObjects(HttpServletRequest request) {
		Cookie[] result = request.getCookies();
		return result != null ? result : new Cookie[0];
	}

	/**
	 * Set Cookie.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param isHttpOnly      true if this cookie is to be marked as HttpOnly, false otherwise
	 * @param response response {@link HttpServletResponse}
	 */
	public static void setCookie(String name, String value, int maxAgeInSeconds, boolean isHttpOnly, HttpServletResponse response) {
		doSetCookie(name, value, maxAgeInSeconds, null, null, isHttpOnly, response);
	}

	/**
	 * Set Cookie.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param response response {@link HttpServletResponse}
	 */
	public static void setCookie(String name, String value, int maxAgeInSeconds, HttpServletResponse response) {
		doSetCookie(name, value, maxAgeInSeconds, null, null, null, response);
	}

	/**
	 * Set Cookie to response.
	 */
	public static void setCookie(Cookie cookie, HttpServletResponse response) {
		response.addCookie(cookie);
	}

	/**
	 * Set Cookie to response.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path            see Cookie.setPath(String)
	 * @param isHttpOnly      true if this cookie is to be marked as HttpOnly, false otherwise
	 * @param response response {@link HttpServletResponse}
	 */
	public static void setCookie(String name, String value, int maxAgeInSeconds, String path, boolean isHttpOnly, HttpServletResponse response) {
		doSetCookie(name, value, maxAgeInSeconds, path, null, isHttpOnly, response);
	}

	/**
	 * Set Cookie to response.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path            see Cookie.setPath(String)
	 * @param response response {@link HttpServletResponse}
	 */
	public static void setCookie(String name, String value, int maxAgeInSeconds, String path, HttpServletResponse response) {
		doSetCookie(name, value, maxAgeInSeconds, path, null, null, response);
	}

	/**
	 * Set Cookie to response.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path            see Cookie.setPath(String)
	 * @param domain          the domain name within which this cookie is visible; form is according to RFC 2109
	 * @param isHttpOnly      true if this cookie is to be marked as HttpOnly, false otherwise
	 * @param response response {@link HttpServletResponse}
	 */
	public static void setCookie(String name, String value, int maxAgeInSeconds, String path, String domain, boolean isHttpOnly, HttpServletResponse response) {
		doSetCookie(name, value, maxAgeInSeconds, path, domain, isHttpOnly, response);
	}

	/**
	 * Remove Cookie.
	 */
	public static void removeCookie(String name, HttpServletResponse response) {
		doSetCookie(name, null, 0, null, null, null, response);
	}

	/**
	 * Remove Cookie.
	 */
	public static void removeCookie(String name, String path, HttpServletResponse response) {
		doSetCookie(name, null, 0, path, null, null, response);
	}

	/**
	 * Remove Cookie.
	 */
	public static void removeCookie(String name, String path, String domain, HttpServletResponse response) {
		doSetCookie(name, null, 0, path, domain, null, response);
	}

	private static void doSetCookie(String name, String value, int maxAgeInSeconds, String path, String domain, Boolean isHttpOnly, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAgeInSeconds);
		/* set the default path value to "/" */
		if (path == null) {
			path = "/";
		}
		cookie.setPath(path);

		if (domain != null) {
			cookie.setDomain(domain);
		}
		if (isHttpOnly != null) {
			cookie.setHttpOnly(isHttpOnly);
		}
		response.addCookie(cookie);
	}

	private static final Object NULL_OBJ = new Object();
	private static final String BODY_STRING_ATTR = "__body_str";

	public static String getBaseUrl(HttpServletRequest request) {
		int port = request.getServerPort();
		return port == 80
			? String.format("%s://%s%s", request.getScheme(), request.getServerName(), request.getContextPath())
			: String.format("%s://%s%s%s", request.getScheme(), request.getServerName(), ":" + port, request.getContextPath());

	}

	/**
	 * get request Content
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @param buffer  buffer
	 * @return String
	 * @throws IOException IOException
	 */
	public static String getRequestStr(HttpServletRequest request, byte[] buffer) throws IOException {
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
			charEncoding = CharsetUtil.UTF_8;
		}
		String str = new String(buffer, charEncoding).trim();
		if (StrUtil.isBlank(str)) {
			StringBuilder sb = new StringBuilder();
			Enumeration<String> parameterNames = request.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String key = parameterNames.nextElement();
				String value = request.getParameter(key);
				sb.append(key).append("=").append(value).append("&");
			}
			str = StrUtil.removeSuffix(sb.toString(), "&");
		}
		return str.replaceAll("&amp;", "&");
	}

	/**
	 * get request bytes
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @return byte[]
	 * @throws IOException IOException
	 */
	public static byte[] getRequestBytes(HttpServletRequest request) throws IOException {
		int contentLength = request.getContentLength();
		if (contentLength < 0) {
			return null;
		}
		byte[] buffer = new byte[contentLength];
		for (int i = 0; i < contentLength; ) {
			int readLen = request.getInputStream().read(buffer, i, contentLength - i);
			if (readLen == -1) {
				break;
			}
			i += readLen;
		}
		return buffer;
	}

	public static String getBodyString(HttpServletRequest request) {
		Object object = getAttr(BODY_STRING_ATTR, request);
		if (object == NULL_OBJ) {
			return null;
		}
		if (object != null) {
			return (String) object;
		}
		object = readData(request);
		setAttr(BODY_STRING_ATTR, object, request);

		return (String) object;
	}

	private static String readData(HttpServletRequest request) {
		BufferedReader br = null;
		try {
			StringBuilder ret;
			br = request.getReader();

			String line = br.readLine();
			if (line != null) {
				ret = new StringBuilder();
				ret.append(line);
			} else {
				return "";
			}

			while ((line = br.readLine()) != null) {
				ret.append('\n').append(line);
			}
			return ret.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	/**
	 * set i18n for the response body
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @param i18n    i18n
	 */
	public static void setI18n(I18n i18n, HttpServletRequest request) {
		setAttr(ATTRIBUTE_RESPONSE_BODY_I18N_KEY, i18n, request);
	}

	/**
	 * get i18n
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @return object [null & string & i18n]
	 */
	public static Object getI18n(HttpServletRequest request) {
		return getAttr(ATTRIBUTE_RESPONSE_BODY_I18N_KEY, request);
	}

	/**
	 * set i18n for the response body
	 *
	 * @param i18nIdentifyName i18n identify name
	 * @param request request {@link HttpServletRequest}          request
	 */
	public static void setI18n(String i18nIdentifyName, HttpServletRequest request) {
		setAttr(ATTRIBUTE_RESPONSE_BODY_I18N_KEY, i18nIdentifyName, request);
	}

	/**
	 * set total for the response body
	 *
	 * @param total   total
	 * @param request request {@link HttpServletRequest} request
	 */
	public static void setTotal(int total, HttpServletRequest request) {
		setAttr(ATTRIBUTE_RESPONSE_BODY_LIST_TOTAL_KEY, total, request);
	}

	/**
	 * get total
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @return value
	 */
	public static Integer getTotal(HttpServletRequest request) {
		return getAttrForInt(ATTRIBUTE_RESPONSE_BODY_LIST_TOTAL_KEY, request);
	}

	/**
	 * get local
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @return local string
	 */
	public static String getLocal(HttpServletRequest request) {
		return request.getLocale().toString();
	}

	/**
	 * get status
	 *
	 * @param request request {@link HttpServletRequest} request
	 * @return HttpStatus {@link HttpStatus}
	 */
	public static HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	/**
	 * wrap resp
	 *
	 * @param data    body data
	 * @param i18n    i18n
	 * @param request request {@link HttpServletRequest} request
	 * @param <T>     body type
	 * @return value
	 */
	public static <T> Resp<T> wrapResp(T data, Object i18n, HttpServletRequest request) {
		Locale locale = LocaleContextHolder.getLocale();
		if (i18n != null) {
			if (i18n instanceof I18n) {
				return RespBuilder.buildHttpSuccess((I18n) i18n, data, RequestUtil.getTotal(request), locale.toString());
			}
			return RespBuilder.buildHttpSuccess(i18n.toString(), data, RequestUtil.getTotal(request), locale.toString());
		}
		return RespBuilder.buildHttpSuccess(Status.Application_Success, data, RequestUtil.getTotal(request), locale.toString());
	}

	/**
	 * wrap resp
	 *
	 * @param data    body data
	 * @param request request {@link HttpServletRequest} request
	 * @param <T>     body type
	 * @return value
	 */
	public static <T> Resp<T> wrapResp(T data, HttpServletRequest request) {
		Object i18n = getI18n(request);
		return RequestUtil.wrapResp(data, i18n, request);
	}
}
