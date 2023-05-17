package com.sunzy.cache.api;

public interface ICacheLoad<K, V>{

    /**
     * 从磁盘加载加载缓存信息
     * @param cache
     */
    void load(final ICache<K, V> cache);
}
