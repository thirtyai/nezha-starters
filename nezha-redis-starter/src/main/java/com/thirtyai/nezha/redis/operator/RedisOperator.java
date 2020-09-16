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
package com.thirtyai.nezha.redis.operator;

import com.thirtyai.nezha.common.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author kyleju
 */
@Getter
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class RedisOperator implements SmartInitializingSingleton {
	private final RedisTemplate<String, Object> redisTemplate;
	private ValueOperations<String, Object> valueOps;
	private HashOperations<String, Object, Object> hashOps;
	private ListOperations<String, Object> listOps;
	private SetOperations<String, Object> setOps;
	private ZSetOperations<String, Object> zSetOps;

	/**
	 * set value
	 *
	 * @param key   key
	 * @param value value
	 */
	public void set(String key, Object value) {
		valueOps.set(key, value);
	}

	/**
	 * set value with expire time ( overwrite )
	 *
	 * @param key     key
	 * @param value   value
	 * @param timeout timeout {@link Duration}
	 */
	public void set(String key, Object value, Duration timeout) {
		valueOps.set(key, value, timeout);
	}

	/**
	 * set value with expire time ( overwrite )
	 *
	 * @param key     key
	 * @param value   value
	 * @param seconds seconds
	 */
	public void set(String key, Object value, Long seconds) {
		valueOps.set(key, value, seconds, TimeUnit.SECONDS);
	}


	/**
	 * get value
	 *
	 * @param key key
	 * @param <T> T
	 * @return value {@link Optional<T>}
	 */
	public <T> Optional<T> get(String key) {
		return Optional.ofNullable((T) valueOps.get(key));
	}

	/**
	 * get value
	 *
	 * @param key    key
	 * @param loader loader {@link Supplier<T>}
	 * @param <T>
	 * @return value {@link Optional<T>}
	 */
	public <T> Optional<T> get(String key, Supplier<T> loader) {
		Optional<T> value = this.get(key);
		if (!value.isPresent()) {
			T newValue = loader.get();
			if (newValue != null) {
				this.set(key, newValue);
			}
			value = Optional.ofNullable(newValue);
		}
		return value;
	}

	/**
	 * delete key
	 *
	 * @param key key
	 * @return boolean
	 */
	public Boolean del(String key) {
		return redisTemplate.delete(key);
	}

	/**
	 * del keys
	 *
	 * @param keys keys
	 * @return value
	 */
	public Long del(String... keys) {
		return del(Arrays.asList(keys));
	}

	/**
	 * del keys
	 *
	 * @param keys keys
	 * @return value
	 */
	public Long del(Collection<String> keys) {
		return redisTemplate.delete(keys);
	}

	/**
	 * search keys
	 * KEYS * match all keys
	 * KEYS h?llo match hello, hallo etc.
	 * KEYS h*llo match hllo, heeeeello etc.
	 * KEYS h[ae]llo match hello, hallo 。
	 *
	 * @param pattern pattern
	 * @return value
	 */
	public Set<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	/**
	 * multi set
	 * at the same time, set multi key-values
	 * <pre>
	 *     multiSet("k1", "v1", "k2", "v2");
	 * </pre>
	 *
	 * @param keysValues keys values
	 */
	public void multiSet(Object... keysValues) {
		valueOps.multiSet(toMap(keysValues));
	}

	/**
	 * multi get
	 *
	 * @param keys keys
	 * @return value {@link List<Object>}
	 */
	public List<Object> multiGet(String... keys) {
		return multiGet(Arrays.asList(keys));
	}

	/**
	 * multi get
	 *
	 * @param keys keys
	 * @return value {@link List<Object>}
	 */
	public List<Object> multiGet(Collection<String> keys) {
		return valueOps.multiGet(keys);
	}

	/**
	 * decrement the key value, the step is 1;
	 * if key not exist, set value 0,then decrement.
	 *
	 * @param key key
	 * @return value
	 */
	public Long decrement(String key) {
		return valueOps.decrement(key);
	}

	/**
	 * decrement the key value, the step is {@param longValue}.
	 * if key not exist, set value 0,then decrement.
	 *
	 * @param key       key
	 * @param longValue longValue
	 * @return value
	 */
	public Long decrement(String key, long longValue) {
		return valueOps.decrement(key, longValue);
	}

	/**
	 * increment the key value, the step is 1;
	 * if key not exist, set value 0,then increment.
	 *
	 * @param key key
	 * @return value
	 */
	public Long increment(String key) {
		return valueOps.increment(key);
	}

	/**
	 * increment the key value, the step is {@param longValue}.
	 * if key not exist, set value 0,then increment.
	 *
	 * @param key       key
	 * @param longValue longValue
	 * @return value
	 */
	public Long increment(String key, long longValue) {
		return valueOps.increment(key, longValue);
	}

	/**
	 * get long
	 *
	 * @param key string
	 * @return value
	 */
	public Long getLong(String key) {
		return Long.valueOf(String.valueOf(valueOps.get(key)));
	}

	/**
	 * exist
	 *
	 * @param key string
	 * @return value
	 */
	public Boolean exist(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * random a key value
	 *
	 * @return value
	 */
	public String randomKey() {
		return redisTemplate.randomKey();
	}

	/**
	 * rename oldKey to newKey, if newKey exist, overwrite it.
	 *
	 * @param oldKey old key
	 * @param newKey new key
	 */
	public void rename(String oldKey, String newKey) {
		redisTemplate.rename(oldKey, newKey);
	}

	/**
	 * move key to db index
	 *
	 * @param key     key
	 * @param dbIndex db index
	 * @return value
	 */
	public Boolean move(String key, int dbIndex) {
		return redisTemplate.move(key, dbIndex);
	}

	/**
	 * expire
	 *
	 * @param key     key
	 * @param seconds seconds
	 * @return value
	 */
	public Boolean expire(String key, long seconds) {
		return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
	}

	/**
	 * expire
	 *
	 * @param key     key
	 * @param timeout timeout
	 * @return value
	 */
	public Boolean expire(String key, Duration timeout) {
		return expire(key, timeout.getSeconds());
	}


	/**
	 * expire at date
	 *
	 * @param key  key
	 * @param date date {@link Date}
	 * @return value
	 */
	public Boolean expireAt(String key, Date date) {
		return redisTemplate.expireAt(key, date);
	}

	/**
	 * expire at unixTime
	 *
	 * @param key      key
	 * @param unixTime unixTime
	 * @return value
	 */
	public Boolean expireAt(String key, long unixTime) {
		return expireAt(key, new Date(unixTime));
	}

	/**
	 * expire milliseconds
	 *
	 * @param key          key
	 * @param milliseconds milliseconds
	 * @return value
	 */
	public Boolean pexpire(String key, long milliseconds) {
		return redisTemplate.expire(key, milliseconds, TimeUnit.MILLISECONDS);
	}

	/**
	 * get set
	 *
	 * @param key   key
	 * @param value value
	 * @param <T>
	 * @return value
	 */
	public <T> T getSet(String key, Object value) {
		return (T) valueOps.getAndSet(key, value);
	}

	/**
	 * persist
	 *
	 * @param key key
	 * @return value
	 */
	public Boolean persist(String key) {
		return redisTemplate.persist(key);
	}

	/**
	 * type the key value's type
	 *
	 * @param key key
	 * @return value type
	 */
	public String type(String key) {
		return redisTemplate.type(key).code();
	}

	/**
	 * ttl
	 *
	 * @param key
	 * @return seconds value
	 */
	public Long ttl(String key) {
		return redisTemplate.getExpire(key);
	}

	/**
	 * millis ttl
	 *
	 * @param key key
	 * @return millis value
	 */
	public Long millisTTL(String key) {
		return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
	}

	/**
	 * hash set
	 *
	 * @param key   key
	 * @param field field
	 * @param value value
	 */
	public void hashSet(String key, Object field, Object value) {
		hashOps.put(key, field, value);
	}

	/**
	 * hash map set
	 *
	 * @param key    key
	 * @param fields fields key-value
	 */
	public void hashMapSet(String key, Map<Object, Object> fields) {
		hashOps.putAll(key, fields);
	}

	/**
	 * hash get
	 *
	 * @param key   key
	 * @param field field
	 * @param <T>
	 * @return value
	 */
	public <T> T hashGet(String key, Object field) {
		return (T) hashOps.get(key, field);
	}

	/**
	 * hash fields get
	 *
	 * @param key    key
	 * @param fields fields
	 * @return value {@link List}
	 */
	public List hashFieldsGet(String key, Object... fields) {
		return hashFieldsGet(key, Arrays.asList(fields));
	}

	/**
	 * hash fields get
	 *
	 * @param key    key
	 * @param fields fields
	 * @return value {@link List}
	 */
	public List hashFieldsGet(String key, Collection<Object> fields) {
		return hashOps.multiGet(key, fields);
	}

	/**
	 * del key
	 *
	 * @param key    key
	 * @param fields fields
	 * @return value
	 */
	public Long hashDel(String key, Object... fields) {
		return hashOps.delete(key, fields);
	}

	/**
	 * hash fields
	 *
	 * @param key   key
	 * @param field field
	 * @return value
	 */
	public Boolean hashExist(String key, Object field) {
		return hashOps.hasKey(key, field);
	}

	/**
	 * get all
	 *
	 * @param key key
	 * @return value {@link Map}
	 */
	public Map hashGetAll(String key) {
		return hashOps.entries(key);
	}


	/**
	 * hash values
	 *
	 * @param key key
	 * @return value
	 */
	public List hashValues(String key) {
		return hashOps.values(key);
	}

	/**
	 * hash fields
	 *
	 * @param key key
	 * @return value {@link Set}
	 */
	public Set<Object> hashFields(String key) {
		return hashOps.keys(key);
	}

	/**
	 * hash fields size
	 *
	 * @param key key
	 * @return value
	 */
	public Long hashFieldsSize(String key) {
		return hashOps.size(key);
	}

	/**
	 * hash increment
	 *
	 * @param key   key
	 * @param field field
	 * @param value value
	 * @return value
	 */
	public Long hashIncrement(String key, Object field, long value) {
		return hashOps.increment(key, field, value);
	}

	/**
	 * hash increment
	 *
	 * @param key   key
	 * @param field field
	 * @param value value
	 * @return value
	 */
	public Double hashIncrement(String key, Object field, double value) {
		return hashOps.increment(key, field, value);
	}

	/**
	 * get list of index
	 *
	 * @param key   key
	 * @param index index
	 * @param <T>
	 * @return T
	 */
	public <T> T listIndex(String key, long index) {
		return (T) listOps.index(key, index);
	}

	/**
	 * list len
	 *
	 * @param key key
	 * @return value
	 */
	public Long listSize(String key) {
		return listOps.size(key);
	}

	/**
	 * list pop
	 *
	 * @param key key
	 * @param <T>
	 * @return value
	 */
	public <T> T listPop(String key) {
		return (T) listOps.leftPop(key);
	}

	/**
	 * list push
	 *
	 * @param key    key
	 * @param values values
	 * @return value
	 */
	public Long listPush(String key, Object... values) {
		return listOps.leftPush(key, values);
	}

	/**
	 * list set
	 *
	 * @param key   key
	 * @param index index
	 * @param value value
	 */
	public void listSet(String key, long index, Object value) {
		listOps.set(key, index, value);
	}

	/**
	 * list remove
	 * count value：
	 * count > 0 : form head start move, move key-value equals {@param value}，move {@param count} times。
	 * count < 0 : form head start move, move key-value equals {@param value}，move absolute {@param count} times。
	 * count = 0 : remove all move key-value equals {@param value}。
	 *
	 * @param key   key
	 * @param count count
	 * @param value value
	 * @return value
	 */
	public Long listRemove(String key, long count, Object value) {
		return listOps.remove(key, count, value);
	}

	/**
	 * list range
	 *
	 * @param key   key
	 * @param start start
	 * @param end   end
	 * @return list {@link List}
	 */
	public List listRange(String key, long start, long end) {
		return listOps.range(key, start, end);
	}

	/**
	 * list trim
	 *
	 * @param key   key
	 * @param start start
	 * @param end   end
	 */
	public void listTrim(String key, long start, long end) {
		listOps.trim(key, start, end);
	}

	/**
	 * remove and return list value
	 *
	 * @param key key
	 * @param <T>
	 * @return value
	 */
	public <T> T rightPop(String key) {
		return (T) listOps.rightPop(key);
	}

	/**
	 * right push
	 *
	 * @param key    key
	 * @param values values
	 * @return value
	 */
	public Long rightPush(String key, Object... values) {
		return listOps.rightPush(key, values);
	}

	/**
	 * right pop and left push
	 *
	 * @param srcKey src key
	 * @param dstKey dst key
	 * @param <T>
	 * @return value
	 */
	public <T> T rightPopAndLeftPush(String srcKey, String dstKey) {
		return (T) listOps.rightPopAndLeftPush(srcKey, dstKey);
	}

	/**
	 * add set
	 *
	 * @param key     key
	 * @param members set {@link Set}
	 * @return value
	 */
	public Long setAdd(String key, Object... members) {
		return setOps.add(key, members);
	}

	/**
	 * remove and return random value of the set
	 *
	 * @param key key
	 * @param <T>
	 * @return
	 */
	public <T> T setPop(String key) {
		return (T) setOps.pop(key);
	}

	/**
	 * set members
	 *
	 * @param key key
	 * @return value
	 */
	public Set setMembers(String key) {
		return setOps.members(key);
	}

	/**
	 * set have member
	 *
	 * @param key    key
	 * @param member member
	 * @return
	 */
	public boolean setHaveMember(String key, Object member) {
		return setOps.isMember(key, member);
	}

	/**
	 * get the intersect of the two key's value set
	 *
	 * @param key      key
	 * @param otherKey other key
	 * @return value
	 */
	public Set sInter(String key, String otherKey) {
		return setOps.intersect(key, otherKey);
	}

	/**
	 * get the intersect of many key's value set
	 *
	 * @param key       key
	 * @param otherKeys other keys
	 * @return value
	 */
	public Set sInter(String key, Collection<String> otherKeys) {
		return setOps.intersect(key, otherKeys);
	}

	/**
	 * get random member
	 *
	 * @param key key
	 * @param <T>
	 * @return value
	 */
	public <T> T setRandomMember(String key) {
		return (T) setOps.randomMember(key);
	}

	/**
	 * get random member of the key's value
	 *
	 * @param key   key
	 * @param count count
	 * @return
	 */
	public List setRandomMember(String key, int count) {
		return setOps.randomMembers(key, count);
	}

	/**
	 * remove member from set
	 *
	 * @param key     key
	 * @param members members
	 * @return value
	 */
	public Long setRemove(String key, Object... members) {
		return setOps.remove(key, members);
	}

	/**
	 * get two set's union
	 *
	 * @param key      key
	 * @param otherKey other key
	 * @return value
	 */
	public Set setUnion(String key, String otherKey) {
		return setOps.union(key, otherKey);
	}

	/**
	 * get many set's union
	 *
	 * @param key       key
	 * @param otherKeys other keys
	 * @return value
	 */
	public Set setUnion(String key, Collection<String> otherKeys) {
		return setOps.union(key, otherKeys);
	}

	/**
	 * get different the two key's value set
	 *
	 * @param key      key
	 * @param otherKey other key
	 * @return value
	 */
	public Set setDifference(String key, String otherKey) {
		return setOps.difference(key, otherKey);
	}

	/**
	 * get different the two key's value set
	 *
	 * @param key       key
	 * @param otherKeys other keys
	 * @return value
	 */
	public Set setDifference(String key, Collection<String> otherKeys) {
		return setOps.difference(key, otherKeys);
	}

	/**
	 * add member and score to the order Set
	 *
	 * @param key    key
	 * @param member member
	 * @param score  score
	 * @return value
	 */
	public Boolean zAdd(String key, Object member, double score) {
		return zSetOps.add(key, member, score);
	}

	/**
	 * add members and score to the order set
	 *
	 * @param key          key
	 * @param scoreMembers score members
	 * @return value
	 */
	public Long zAdd(String key, Map<Object, Double> scoreMembers) {
		Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
		scoreMembers.forEach((k, v) -> {
			tuples.add(new DefaultTypedTuple<>(k, v));
		});
		return zSetOps.add(key, tuples);
	}

	/**
	 * z card
	 *
	 * @param key key
	 * @return value
	 */
	public Long zCard(String key) {
		return zSetOps.zCard(key);
	}

	/**
	 * get the score in [min, max] 's values count
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return count
	 */
	public Long zCount(String key, double min, double max) {
		return zSetOps.count(key, min, max);
	}

	/**
	 * increment score for the member
	 *
	 * @param key    key
	 * @param member member
	 * @param score  score
	 * @return value
	 */
	public Double incrementScore(String key, Object member, double score) {
		return zSetOps.incrementScore(key, member, score);
	}

	/**
	 * get the range (min-max)
	 *
	 * @param key   key
	 * @param start start
	 * @param end   end
	 * @return value
	 */
	public Set zRange(String key, long start, long end) {
		return zSetOps.range(key, start, end);
	}

	/**
	 * get reverse range (max-min)
	 *
	 * @param key   key
	 * @param start start
	 * @param end   end
	 * @return value
	 */
	public Set zReverseRange(String key, long start, long end) {
		return zSetOps.reverseRange(key, start, end);
	}

	/**
	 * get the range by score (min -> max)
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return value
	 */
	public Set zRangeByScore(String key, double min, double max) {
		return zSetOps.rangeByScore(key, min, max);
	}

	/**
	 * get rank number (max - min)
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public Long zRank(String key, Object member) {
		return zSetOps.rank(key, member);
	}

	/**
	 * get rank number (max - min)
	 *
	 * @param key    key
	 * @param member member
	 * @return value
	 */
	public Long zReverseRank(String key, Object member) {
		return zSetOps.reverseRank(key, member);
	}

	/**
	 * remove some members
	 *
	 * @param key     key
	 * @param members members
	 * @return count
	 */
	public Long zRemove(String key, Object... members) {
		return zSetOps.remove(key, members);
	}

	/**
	 * get score value
	 *
	 * @param key    key
	 * @param member member
	 * @return value
	 */
	public Double zScore(String key, Object member) {
		return zSetOps.score(key, member);
	}

	/**
	 * publish message queue
	 *
	 * @param channelTopic channel topic
	 * @param message      message
	 */
	public void publish(ChannelTopic channelTopic, Object message) {
		redisTemplate.convertAndSend(channelTopic.toString(), Objects.requireNonNull(JsonUtil.toJson(message)));
		log.debug("redis publish [{}] message to [{}]", message.toString(), channelTopic.toString());
	}

	@Override
	public void afterSingletonsInstantiated() {
		Assert.notNull(redisTemplate, "redisTemplate is null");
		valueOps = redisTemplate.opsForValue();
		hashOps = redisTemplate.opsForHash();
		listOps = redisTemplate.opsForList();
		setOps = redisTemplate.opsForSet();
		zSetOps = redisTemplate.opsForZSet();
	}

	private <K, V> Map<K, V> toMap(Object... keysValues) {
		int kvLength = keysValues.length;
		if (kvLength % 2 != 0) {
			throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
		}
		Map<K, V> keyValueMap = new HashMap<>(kvLength);
		for (int i = kvLength - 2; i >= 0; i -= 2) {
			Object key = keysValues[i];
			Object value = keysValues[i + 1];
			keyValueMap.put((K) key, (V) value);
		}
		return keyValueMap;
	}
}
