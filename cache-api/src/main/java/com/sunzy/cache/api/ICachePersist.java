package com.sunzy.cache.api;

import java.util.concurrent.TimeUnit;

public interface ICachePersist<K, V>{
    /**
     * 持久化缓存信息
     * @param cache 缓存
     * @since 0.0.7
     */
    void persist(final ICache<K, V> cache);

    public long delay();

    public long period();

    public TimeUnit timeUnit();
}
