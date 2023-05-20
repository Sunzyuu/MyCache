package com.sunzy.cache.core.evict;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvict;
import com.sunzy.cache.api.ICacheEvictContext;
import com.sunzy.cache.core.common.CacheEntry;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheEvictLruLinkedHashMap<K, V> extends LinkedHashMap<K,V> implements ICacheEvict<K,V> {

    private static final Log log = LogFactory.getLog(CacheEvictLruLinkedHashMap.class);


    /**
     * 是否移除标识
     */
    private volatile boolean removeFlag = false;

    /**
     * 最旧的一个元素
     */
    private transient Map.Entry<K, V> eldest = null;

    public CacheEvictLruLinkedHashMap() {
        super(16, 0.75f, true);
    }

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除队尾的元素
        if(cache.size() >= context.size()) {
            removeFlag = true;

            // 执行 put 操作
            super.put(context.key(), null);

            // 构建淘汰的元素
            K evictKey = eldest.getKey();
            // 从缓存中移除
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        } else {
            removeFlag = false;
        }
        return result;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        this.eldest = eldest;
        return removeFlag;
    }

    @Override
    public void updateKey(K key) {
        super.put(key, null);
    }


    @Override
    public void removeKey(K key) {
        super.remove(key);
    }
}
