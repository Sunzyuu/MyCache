package com.sunzy.cache.core.evict;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvict;
import com.sunzy.cache.api.ICacheEvictContext;
import com.sunzy.cache.core.common.CacheEntry;
import com.sunzy.cache.core.support.struct.lru.impl.LruMapDoubleList;

public class CacheEvictLru2<K, V> extends AbstractCacheEvict<K, V> {

    private static Log log = LogFactory.getLog(CacheEvictLru2.class);

    /**
     * 访问一次的key
     */
    private LruMapDoubleList<K, V> firstLruMap;

    /**
     * 访问一次以上的key
     */
    private LruMapDoubleList<K, V> moreLruMap;

    public CacheEvictLru2() {
        this.firstLruMap = new LruMapDoubleList<>();
        this.moreLruMap = new LruMapDoubleList<>();
    }


    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        ICache<K, V> cache = context.cache();
        if(cache.size() >= context.size()){
            ICacheEntry<K, V> evictEntry = null;
            if(!firstLruMap.isEmpty()){
                evictEntry = firstLruMap.removeEldest();
                log.debug("从 firstLruMap 中淘汰数据：{}", evictEntry.key());
            } else {
                evictEntry = moreLruMap.removeEldest();
                log.debug("从 moreLruMap 中淘汰数据：{}", evictEntry.key());
            }
            // 缓存移除
            final K evictKey = evictEntry.key();
            V evictValue =  cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }
        return result;
    }

    @Override
    public void updateKey(K key) {
        if(moreLruMap.contains(key) || firstLruMap.contains(key)) {
            this.removeKey(key);
            moreLruMap.updateKey(key);
            log.debug("key: {} 多次访问，加入到 moreLruMap 中", key);
        } else {
            firstLruMap.updateKey(key);
            log.debug("key: {} 第一次访问，加入到 firstLruMap 中", key);
        }
    }

    @Override
    public void removeKey(K key) {
        if(moreLruMap.contains(key)){
            moreLruMap.removeKey(key);
            log.debug("key: {} 从 moreLruMap 中移除", key);
        } else {
            firstLruMap.removeKey(key);
            log.debug("key: {} 从 firstLruMap 中移除", key);
        }
    }
}
