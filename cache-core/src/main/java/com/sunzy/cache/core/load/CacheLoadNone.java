package com.sunzy.cache.core.load;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheLoad;

public class CacheLoadNone<K, V> implements ICacheLoad<K, V> {
    @Override
    public void load(ICache<K, V> cache) {
        System.out.println("load data...");
    }
}
