package com.sunzy.cache.core.evict;

import com.sunzy.cache.api.*;
import com.sunzy.cache.core.common.CacheEntry;
import com.sunzy.cache.core.constant.enums.CacheRemoveType;
import com.sunzy.cache.core.support.listener.remove.CacheRemoveListenerContext;

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
            // 移除最开始的元素 利用的是 队列的后进先出的性质，所以删除的是最早加入map中的元素
            V evictValue = cache.remove(evictKey);

            // 执行淘汰监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(evictKey).value(evictValue).type(CacheRemoveType.EVICT.code());
            for(ICacheRemoveListener<K,V> listener : cache.removeListeners()) {
                listener.listen(removeListenerContext);
            }
            result = new CacheEntry<>(evictKey, evictValue);
        }

        // 将新加的元素放入队尾
        final K key = context.key();
        queue.add(key);

        return result;
    }

    @Override
    public void update(K key) {

    }

    @Override
    public void remove(K key) {

    }
}
