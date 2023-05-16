package com.sunzy.cache.core.core;

import com.sunzy.cache.api.ICacheContext;
import com.sunzy.cache.api.ICacheEvict;

import java.util.Map;

/**
 * 缓存上下文，在创建Cache对象时使用
 * @param <K>
 * @param <V>
 */
public class CacheContext<K, V> implements ICacheContext<K, V> {
    private Map<K, V> map;

    private int size;

    private ICacheEvict<K, V> cacheEvict;



    @Override
    public Map<K, V> map() {
        return map;
    }

    public CacheContext<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    @Override
    public int size() {
        return size;
    }

    public CacheContext<K, V> size(int size) {
        this.size = size;
        return this;
    }

    @Override
    public ICacheEvict<K, V> cacheEvict() {
        return cacheEvict;
    }

    public CacheContext<K, V> cacheEvict(ICacheEvict<K, V> cacheEvict) {
        this.cacheEvict = cacheEvict;
        return this;
    }

}
