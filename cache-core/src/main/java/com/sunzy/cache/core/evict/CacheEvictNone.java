package com.sunzy.cache.core.evict;

import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvict;
import com.sunzy.cache.api.ICacheEvictContext;

/**
 * 丢弃策略
 * @author binbin.hou
 * @since 0.0.2
 */
public class CacheEvictNone<K,V> implements ICacheEvict<K, V> {

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext<K, V> context) {
        return null;
    }
}
