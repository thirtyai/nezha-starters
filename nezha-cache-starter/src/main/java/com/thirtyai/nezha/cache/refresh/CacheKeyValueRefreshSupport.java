package com.thirtyai.nezha.cache.refresh;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.thirtyai.nezha.cache.MultipleCache;
import com.thirtyai.nezha.cache.MultipleCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * auto refresh support
 *
 * @author kyleju
 */
@Slf4j
@RequiredArgsConstructor
public class CacheKeyValueRefreshSupport implements ICacheKeyValueRefreshSupport {
	private static final int TWO = 2;
	/**
	 * need auto refresh caches
	 */
	private Map<String, CacheKeyValueRefreshRunnable> runnableRefreshers = MapUtil.newHashMap();

	@NonNull
	private final MultipleCacheManager cacheManager;
	@NonNull
	private final ThreadPoolTaskExecutor taskExecutor;

	@Override
	public void add(String key, Object targetBean, Method targetMethod, Object[] arguments, List<String> cacheNames) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : arguments) {
			sb.append(obj.toString());
		}

		cacheNames.forEach(cacheName -> {
			if (StrUtil.isNotBlank(cacheName)) {
				MultipleCache multipleCache = cacheManager.getCache(cacheName);
				if (multipleCache != null) {
					RefreshCacheKeyValue refreshCacheKeyValue = new RefreshCacheKeyValue();
					refreshCacheKeyValue.setName(cacheName);
					refreshCacheKeyValue.setMultipleCache(multipleCache);
					int ttl = refreshCacheKeyValue.getMultipleCache().getCacheItem().getTtlSeconds();
					int rts = refreshCacheKeyValue.getMultipleCache().getCacheItem().getRefreshTriggerSeconds();
					int doubleSleepTime = refreshCacheKeyValue.getMultipleCache().getCacheItem().getRefresherSleepSeconds() * 2;
					/*
					ttl > rts + sleepTime *2
					2rts >ttl
					ttl + 2rts > rts + 2* sleepTime  => rts > 2 * sleepTime
					ttl + 2rts > 2rts + 4* sleepTime => ttl > 4 * sleepTime
					rts> 2 * sleepTime
					ttl> 4 * sleepTime
					ttl> rts > ttl/2
					 */
					if (ttl > 0 && rts >= ttl / TWO && (ttl - rts) >= doubleSleepTime) {
						String keyIdentity = refreshCacheKeyValue.getName() + StrUtil.COLON + SecureUtil.md5(sb.toString());
						final CacheKeyValueRefreshRunnable cacheKeyValueRefreshRunnable = new CacheKeyValueRefreshRunnable(key, targetBean, targetMethod, arguments, refreshCacheKeyValue, multipleCache);
						if (!runnableRefreshers.containsKey(keyIdentity)) {
							this.runnableRefreshers.put(keyIdentity, cacheKeyValueRefreshRunnable);
							this.run(cacheKeyValueRefreshRunnable);
						}
					}
				}
			}
		});
	}

	@Override
	public void run(Runnable runnable) {
		taskExecutor.execute(runnable);
	}
}
