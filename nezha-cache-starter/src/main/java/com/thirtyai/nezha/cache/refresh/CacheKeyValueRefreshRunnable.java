package com.thirtyai.nezha.cache.refresh;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.thirtyai.nezha.cache.MultipleCache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Cache key value refresh runnable
 *
 * @author kyleju
 */
@Getter
@Slf4j
@RequiredArgsConstructor
public class CacheKeyValueRefreshRunnable implements Runnable {
	private Object key;
	private Object targetBean;
	private Method targetMethod;
	private Object[] arguments;
	private RefreshCacheKeyValue refreshCacheValue;
	private MultipleCache multipleCache;

	public CacheKeyValueRefreshRunnable(Object key, Object targetBean, Method targetMethod, Object[] arguments, RefreshCacheKeyValue refreshCacheKeyValue, MultipleCache multipleCache) {
		this.key = key;
		this.targetBean = targetBean;
		this.targetMethod = targetMethod;
		this.refreshCacheValue = refreshCacheKeyValue;
		this.multipleCache = multipleCache;
		if (arguments != null && arguments.length != 0) {
			this.arguments = Arrays.copyOf(arguments, arguments.length);
		}
	}

	private Object invoke() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		final MethodInvoker invoker = new MethodInvoker();
		invoker.setTargetObject(this.getTargetBean());
		invoker.setArguments(this.getArguments());
		invoker.setTargetMethod(this.getTargetMethod().getName());
		invoker.prepare();
		return invoker.invoke();
	}

	private void refreshCache() {
		boolean invocationSuccess;
		Object computed = null;

		try {
			computed = invoke();
			invocationSuccess = true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			invocationSuccess = false;
		}

		if (invocationSuccess) {
			if (multipleCache != null) {
				multipleCache.put(this.getKey(), computed);
				log.info("refresh support: had refresh {}, value is {}.", multipleCache.getCacheItem().getName(), computed);
			}
		}
	}

	@Override
	public void run() {
		DateTime dateTime = DateTime.now();
		int remainTime = 0;
		while (true) {
			if (remainTime >= this.refreshCacheValue.getMultipleCache().getCacheItem().getRefreshTriggerSeconds()) {
				this.refreshCache();
				remainTime = 0;
			}
			ThreadUtil.sleep(1000 * this.refreshCacheValue.getMultipleCache().getCacheItem().getRefresherSleepSeconds());
			remainTime = remainTime + Convert.toInt(DateUtil.between(DateTime.now(), dateTime, DateUnit.SECOND));
			dateTime = DateTime.now();
		}
	}
}

