package com.sunzy.cache.core.evict;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvict;
import com.sunzy.cache.api.ICacheEvictContext;
import com.sunzy.cache.core.common.CacheEntry;

import java.util.LinkedList;
import java.util.Queue;

public class CacheEvictFIFO<K,V> implements ICacheEvict<K, V> {


    private final Queue<K> queue = new LinkedList<>();

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext<K, V> context) {
        CacheEntry<K,V> result = null;

        final ICache<K,V> cache = context.cache();
        // 超过限制，执行移除
        if(cache.size() >= context.size()) {
            K evictKey = queue.remove();
            // 移除最开始的元素
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        // 将新加的元素放入队尾
        final K key = context.key();
        queue.add(key);

        return result;
    }
}
