package com.sunzy.cache.core.evict;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvict;
import com.sunzy.cache.api.ICacheEvictContext;
import com.sunzy.cache.core.common.CacheEntry;
import com.sunzy.cache.core.support.struct.lru.ILruMap;
import com.sunzy.cache.core.support.struct.lru.impl.LruMapCircleList;

public class CacheEvictClock<K, V> extends AbstractCacheEvict<K, V>{
    private static final Log log = LogFactory.getLog(CacheEvictClock.class);

    /**
     * 循环链表
     */
    private final ILruMap<K,V> circleList;

    public CacheEvictClock() {
        this.circleList = new LruMapCircleList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        ICache<K, V> cache = context.cache();
        if(cache.size() >= context.size()) {
            ICacheEntry<K, V> evictEntry = circleList.removeEldest();
            final K evictKey = evictEntry.key();
            V evictValue = cache.remove(evictKey);
            log.debug("基于 clock 算法淘汰 key：{}, value: {}", evictKey, evictValue);

            result = new CacheEntry<>(evictKey, evictValue);
        }
        return result;
    }


    @Override
    public void updateKey(K key) {
        this.circleList.updateKey(key);
    }

    @Override
    public void removeKey(K key) {
        this.circleList.removeKey(key);
    }

}
