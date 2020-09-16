package com.thirtyai.nezha.cache.refresh;

import java.lang.reflect.Method;
import java.util.List;

/**
 * cache refresh support
 *
 * @author kyleju
 */
public interface ICacheKeyValueRefreshSupport {

	/**
	 * add refresh
	 *
	 * @param key                 key
	 * @param invokedBean         invoked bean
	 * @param invokedMethod       invoked method
	 * @param invocationArguments invocation arguments
	 * @param cacheNames          cache names
	 */
	void add(String key, Object invokedBean, Method invokedMethod, Object[] invocationArguments, List<String> cacheNames);

	/**
	 * refresh run
	 *
	 * @param runnable runnable
	 */
	void run(Runnable runnable);
}
