package com.sunzy.cache.api;

import java.util.Collection;

/**
 * 缓存过期接口
 * @param <K>
 * @param <V>
 */
public interface ICacheExpire<K, V> {

    /**
     * 指定过期时间
     * @param key
     * @param expireAt 什么时候过期
     */
    void expire(K key, final long expireAt);


    /**
     * 刷新过期时间
     * @param keyList
     */
    void refreshExpired(final Collection<K> keyList);


    /**
     * 待过期的 key
     * 不存在，则返回 null
     * @param key 待过期的 key
     * @return 结果
     */
    Long expireTime(final K key);
}
