package com.sunzy.cache.core.evict;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvict;
import com.sunzy.cache.api.ICacheEvictContext;
import com.sunzy.cache.core.common.CacheEntry;

import java.util.ArrayList;
import java.util.List;

public class CacheEvictLRU<K, V> implements ICacheEvict<K, V> {
    private static final Log log = LogFactory.getLog(CacheEvictLRU.class);

    /**
     * list信息
     */
    private final List<K> list = new ArrayList<K>();

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        ICache<K, V> cache = context.cache();
        // 超出限制 移除队尾的元素
        if(cache.size() >= context.size()){
            K evictKey = list.get(list.size() - 1);
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }
        return result;
    }

    /**
     * 放入元素
     * （1） 删除已存在的元素
     * （2） 新元素放到队头
     * @param key key
     */
    @Override
    public void update(K key) {
        this.list.remove(key);
        this.list.add(0, key);
    }

    /**
     * 移除元素
     * @param key key
     */
    @Override
    public void remove(K key) {
        this.list.remove(key);
    }
}
