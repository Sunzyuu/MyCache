package com.sunzy.cache.core.support.struct.lru;

import com.sunzy.cache.api.ICacheEntry;

/**
 * LRU map接口
 */
public interface ILruMap<K, V> {

    /**
     * 移除最老的元素
     * @return
     */
    ICacheEntry<K, V> removeEldest();
    /**
     * 更新 key 的信息
     * @param key key
     * @since 0.0.13
     */
    void updateKey(final K key);

    /**
     * 移除对应的 key 信息
     * @param key key
     */
    void removeKey(final K key);

    /**
     * 是否为空
     * @return 是否
     */
    boolean isEmpty();

    /**
     * 是否包含元素
     * @param key 元素
     * @return 结果
     */
    boolean contains(final K key);



}
