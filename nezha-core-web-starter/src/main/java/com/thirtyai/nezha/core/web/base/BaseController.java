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
package com.thirtyai.nezha.core.web.base;

import cn.hutool.core.map.MapUtil;
import com.thirtyai.nezha.common.i18n.I18n;
import com.thirtyai.nezha.common.wrap.Resp;
import com.thirtyai.nezha.core.web.utils.RequestUtil;
import com.thirtyai.nezha.i18n.Status;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kyleju
 */
public class BaseController {

	protected HttpServletResponse httpResponse;
	protected HttpServletRequest httpRequest;

	@Autowired
	private void init(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		httpRequest = httpServletRequest;
		httpResponse = httpServletResponse;
	}

	/**
	 * Get ip
	 *
	 * @return
	 */
	public String getIpAddress() {
		return RequestUtil.getIpAddress(httpRequest);
	}

	/**
	 * Multipart Request check
	 *
	 * @return
	 */
	public boolean isMultipartRequest() {
		return RequestUtil.isMultipartRequest(httpRequest);
	}


	/**
	 * Stores an attribute in this request
	 *
	 * @param name  a String specifying the name of the attribute
	 * @param value the Object to be stored
	 */
	public BaseController setAttr(String name, Object value) {
		RequestUtil.setAttr(name, value, this.httpRequest);
		return this;
	}

	/**
	 * Removes an attribute from this request
	 *
	 * @param name a String specifying the name of the attribute to remove
	 */
	public BaseController removeAttr(String name) {
		RequestUtil.removeAttr(name, this.httpRequest);
		return this;
	}

	/**
	 * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
	 *
	 * @param attrMap key and value as attribute of the map to be stored
	 */
	public BaseController setAttrs(Map<String, Object> attrMap) {
		RequestUtil.setAttrs(attrMap, this.httpRequest);
		return this;
	}

	/**
	 * Returns the value of a request parameter as a String, or null if the parameter does not exist.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a String representing the single value of the parameter
	 */
	public String getPara(String name) {
		return RequestUtil.getPara(name, this.httpRequest);
	}

	/**
	 * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
	 *
	 * @param name         a String specifying the name of the parameter
	 * @param defaultValue a String value be returned when the value of parameter is null
	 * @return a String representing the single value of the parameter
	 */
	public String getPara(String name, String defaultValue) {
		return RequestUtil.getPara(name, defaultValue, this.httpRequest);
	}

	/**
	 * Returns the values of the request parameters as a Map.
	 *
	 * @return a Map contains all the parameters name and value
	 */
	public Map<String, String[]> getParaMap() {
		return RequestUtil.getParaMap(this.httpRequest);
	}

	/**
	 * Returns an Enumeration of String objects containing the names of the parameters
	 * contained in this request. If the request has no parameters, the method returns
	 * an empty Enumeration.
	 *
	 * @return an Enumeration of String objects, each String containing the name of
	 * a request parameter; or an empty Enumeration if the request has no parameters
	 */
	public Enumeration<String> getParaNames() {
		return RequestUtil.getParaNames(this.httpRequest);
	}

	/**
	 * Returns an array of String objects containing all of the values the given request
	 * parameter has, or null if the parameter does not exist. If the parameter has a
	 * single value, the array has a length of 1.
	 *
	 * @param name a String containing the name of the parameter whose value is requested
	 * @return an array of String objects containing the parameter's values
	 */
	public String[] getParaValues(String name) {
		return RequestUtil.getParaValues(name, this.httpRequest);
	}

	/**
	 * Returns an array of Integer objects containing all of the values the given request
	 * parameter has, or null if the parameter does not exist. If the parameter has a
	 * single value, the array has a length of 1.
	 *
	 * @param name a String containing the name of the parameter whose value is requested
	 * @return an array of Integer objects containing the parameter's values
	 */
	public Integer[] getParaValuesToInt(String name) {
		return RequestUtil.getParaValuesToInt(name, this.httpRequest);
	}

	public Long[] getParaValuesToLong(String name) {
		return RequestUtil.getParaValuesToLong(name, this.httpRequest);
	}

	/**
	 * Returns an Enumeration containing the names of the attributes available to this request.
	 * This method returns an empty Enumeration if the request has no attributes available to it.
	 *
	 * @return an Enumeration of strings containing the names of the request's attributes
	 */
	public Enumeration<String> getAttrNames() {
		return RequestUtil.getAttrNames(this.httpRequest);
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 *
	 * @param name a String specifying the name of the attribute
	 * @return an Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public <T> T getAttr(String name) {
		return (T) RequestUtil.getAttr(name, this.httpRequest);
	}

	public <T> T getAttr(String name, T defaultValue) {
		return (T) RequestUtil.getAttr(name, defaultValue, this.httpRequest);
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 *
	 * @param name a String specifying the name of the attribute
	 * @return an String Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public String getAttrForStr(String name) {
		return RequestUtil.getAttrForStr(name, this.httpRequest);
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 *
	 * @param name a String specifying the name of the attribute
	 * @return an Integer Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public Integer getAttrForInt(String name) {
		return RequestUtil.getAttrForInt(name, this.httpRequest);
	}

	/**
	 * Returns the value of the specified request header as a String.
	 */
	public String getHeader(String name) {
		return RequestUtil.getHeader(name, this.httpRequest);
	}


	/**
	 * Returns the value of a request parameter and convert to Integer.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Integer getParaToInt(String name) {
		return RequestUtil.getParaToInt(name, this.httpRequest);
	}

	/**
	 * Returns the value of a request parameter and convert to Integer with a default value if it is null.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Integer getParaToInt(String name, Integer defaultValue) {
		return RequestUtil.getParaToInt(name, defaultValue, this.httpRequest);
	}


	/**
	 * Returns the value of a request parameter and convert to Long.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Long getParaToLong(String name) {
		return RequestUtil.getParaToLong(name, this.httpRequest);
	}

	/**
	 * Returns the value of a request parameter and convert to Long with a default value if it is null.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Long getParaToLong(String name, Long defaultValue) {
		return RequestUtil.getParaToLong(name, defaultValue, this.httpRequest);
	}

	/**
	 * Returns the value of a request parameter and convert to Boolean.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", null if parameter is not exists
	 */
	public Boolean getParaToBoolean(String name) {
		return RequestUtil.getParaToBoolean(name, this.httpRequest);
	}

	/**
	 * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", default value if it is null
	 */
	public Boolean getParaToBoolean(String name, Boolean defaultValue) {
		return RequestUtil.getParaToBoolean(name, defaultValue, this.httpRequest);
	}

	/**
	 * Returns the value of a request parameter and convert to Date.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a Date representing the single value of the parameter
	 */
	public Date getParaToDate(String name) {
		return RequestUtil.getParaToDate(name, this.httpRequest);
	}

	/**
	 * Returns the value of a request parameter and convert to Date with a default value if it is null.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a Date representing the single value of the parameter
	 */
	public Date getParaToDate(String name, Date defaultValue) {
		return RequestUtil.getParaToDate(name, defaultValue, this.httpRequest);
	}

	/**
	 * Return HttpSession.
	 */
	public HttpSession getSession() {
		return RequestUtil.getSession(this.httpRequest);
	}

	/**
	 * Return HttpSession.
	 *
	 * @param create a boolean specifying create HttpSession if it not exists
	 */
	public HttpSession getSession(boolean create) {
		return RequestUtil.getSession(create, this.httpRequest);
	}

	/**
	 * Return a Object from session.
	 *
	 * @param key a String specifying the key of the Object stored in session
	 */
	public <T> T getSessionAttr(String key) {
		return RequestUtil.getSessionAttr(key, this.httpRequest);
	}

	public <T> T getSessionAttr(String key, T defaultValue) {
		return RequestUtil.getSessionAttr(key, defaultValue, this.httpRequest);
	}

	/**
	 * Store Object to session.
	 *
	 * @param key   a String specifying the key of the Object stored in session
	 * @param value a Object specifying the value stored in session
	 */
	public BaseController setSessionAttr(String key, Object value) {
		RequestUtil.setSessionAttr(key, value, this.httpRequest);
		return this;
	}

	/**
	 * Remove Object in session.
	 *
	 * @param key a String specifying the key of the Object stored in session
	 */
	public BaseController removeSessionAttr(String key) {
		RequestUtil.removeSessionAttr(key, this.httpRequest);
		return this;
	}

	/**
	 * Get cookie value by cookie name.
	 */
	public String getCookie(String name, String defaultValue) {
		return RequestUtil.getCookie(name, defaultValue, this.httpRequest);
	}

	/**
	 * Get cookie value by cookie name.
	 */
	public String getCookie(String name) {
		return RequestUtil.getCookie(name, this.httpRequest);
	}

	/**
	 * Get cookie value by cookie name and convert to Integer.
	 */
	public Integer getCookieToInt(String name) {
		return RequestUtil.getCookieToInt(name, this.httpRequest);
	}

	/**
	 * Get cookie value by cookie name and convert to Integer.
	 */
	public Integer getCookieToInt(String name, Integer defaultValue) {
		return RequestUtil.getCookieToInt(name, defaultValue, this.httpRequest);
	}

	/**
	 * Get cookie value by cookie name and convert to Long.
	 */
	public Long getCookieToLong(String name) {
		return RequestUtil.getCookieToLong(name, this.httpRequest);
	}

	/**
	 * Get cookie value by cookie name and convert to Long.
	 */
	public Long getCookieToLong(String name, Long defaultValue) {
		return RequestUtil.getCookieToLong(name, this.httpRequest);
	}

	/**
	 * Get cookie object by cookie name.
	 */
	public Cookie getCookieObject(String name) {
		return RequestUtil.getCookieObject(name, this.httpRequest);
	}

	/**
	 * Get all cookie objects.
	 */
	public Cookie[] getCookieObjects() {
		return RequestUtil.getCookieObjects(this.httpRequest);
	}

	/**
	 * Set Cookie.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param isHttpOnly      true if this cookie is to be marked as HttpOnly, false otherwise
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds, boolean isHttpOnly) {
		RequestUtil.setCookie(name, value, maxAgeInSeconds, isHttpOnly, this.httpResponse);
		return this;
	}

	/**
	 * Set Cookie.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds) {
		RequestUtil.setCookie(name, value, maxAgeInSeconds, this.httpResponse);
		return this;
	}

	/**
	 * Set Cookie to response.
	 */
	public BaseController setCookie(Cookie cookie) {
		RequestUtil.setCookie(cookie, this.httpResponse);
		return this;
	}

	/**
	 * Set Cookie to response.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path            see Cookie.setPath(String)
	 * @param isHttpOnly      true if this cookie is to be marked as HttpOnly, false otherwise
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds, String path, boolean isHttpOnly) {
		RequestUtil.setCookie(name, value, maxAgeInSeconds, path, isHttpOnly, this.httpResponse);
		return this;
	}

	/**
	 * Set Cookie to response.
	 *
	 * @param name            cookie name
	 * @param value           cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path            see Cookie.setPath(String)
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds, String path) {
		RequestUtil.setCookie(name, value, maxAgeInSeconds, path, this.httpResponse);
		return this;
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
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds, String path, String domain, boolean isHttpOnly) {
		RequestUtil.setCookie(name, value, maxAgeInSeconds, path, domain, isHttpOnly, this.httpResponse);
		return this;
	}

	/**
	 * Remove Cookie.
	 */
	public BaseController removeCookie(String name) {
		RequestUtil.removeCookie(name, this.httpResponse);
		return this;
	}

	/**
	 * Remove Cookie.
	 */
	public BaseController removeCookie(String name, String path) {
		RequestUtil.removeCookie(name, path, this.httpResponse);
		return this;
	}

	/**
	 * Remove Cookie.
	 */
	public BaseController removeCookie(String name, String path, String domain) {
		RequestUtil.removeCookie(name, path, domain, this.httpResponse);
		return this;
	}

	/**
	 * Is Mobile Browser
	 *
	 * @return
	 */
	public boolean isMobileBrowser() {
		return RequestUtil.isMobileBrowser(this.httpRequest);
	}

	/**
	 * Is Wechat Browser
	 *
	 * @return
	 */
	public boolean isWechatBrowser() {
		return RequestUtil.isWechatBrowser(this.httpRequest);
	}

	/**
	 * Is IE Browser
	 *
	 * @return
	 */
	public boolean isIeBrowser() {
		return RequestUtil.isIEBrowser(this.httpRequest);
	}

	/**
	 * Is Ajax Request
	 *
	 * @return
	 */
	public boolean isAjaxRequest() {
		return RequestUtil.isAjaxRequest(this.httpRequest);
	}

	/**
	 * Get referer
	 *
	 * @return
	 */
	public String getReferer() {
		return RequestUtil.getReferer(this.httpRequest);
	}


	/**
	 * Get UA
	 *
	 * @return
	 */
	public String getUserAgent() {
		return RequestUtil.getUserAgent(this.httpRequest);
	}

	protected HashMap<String, Object> flash;

	public BaseController setFlashAttr(String name, Object value) {
		if (flash == null) {
			flash = MapUtil.newHashMap();
		}

		flash.put(name, value);
		return this;
	}

	public BaseController setFlashMap(Map map) {
		if (map == null) {
			throw new NullPointerException("map is null");
		}
		if (flash == null) {
			flash = MapUtil.newHashMap();
		}

		flash.putAll(map);
		return this;
	}

	public <T> T getFlashAttr(String name) {
		return flash == null ? null : (T) flash.get(name);
	}

	public HashMap<String, Object> getFlashAttrs() {
		return flash;
	}

	public String getBaseUrl() {
		return RequestUtil.getBaseUrl(this.httpRequest);
	}

	public String getBodyString() {
		return RequestUtil.getBodyString(this.httpRequest);
	}

	/**
	 * set i18n for the response body
	 *
	 * @param i18n i18n
	 */
	public void setI18n(I18n i18n) {
		RequestUtil.setI18n(i18n, httpRequest);
	}

	/**
	 * set i18n for the response body
	 *
	 * @param i18nIdentifyName i18n identify name
	 */
	public void setI18n(String i18nIdentifyName) {
		RequestUtil.setI18n(i18nIdentifyName, httpRequest);
	}

	/**
	 * get i18n
	 *
	 * @return object [null & string & i18n]
	 */
	public Object getI18n() {
		return RequestUtil.getI18n(httpRequest);
	}

	/**
	 * set total for the response body
	 *
	 * @param total total
	 */
	public void setTotal(int total) {
		RequestUtil.setTotal(total, httpRequest);
	}

	/**
	 * wrap resp
	 *
	 * @param data data
	 * @param i18n i18n
	 * @param <T>  T object
	 * @return value
	 */
	public <T> Resp<T> wrapResp(T data, Object i18n) {
		return RequestUtil.wrapResp(data, i18n, this.httpRequest);
	}

	/**
	 * wrap resp
	 *
	 * @param data data
	 * @param <T>  T object
	 * @return value
	 */
	public <T> Resp<T> wrapResp(T data) {
		Object i18n = this.getI18n();
		if (i18n == null) {
			i18n = Status.Application_Success;
		}
		return RequestUtil.wrapResp(data, i18n, this.httpRequest);
	}
}
