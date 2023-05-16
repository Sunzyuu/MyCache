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


    void refreshExpired(final Collection<K> keyList);
}
