package com.sunzy.cache.api;


public interface ICacheEvict<K, V> {
    /**
     * 驱除策略
     *
     * @param context 上下文
     * @since 0.0.2
     * @return 被移除的明细，没有时返回 null
     */
    ICacheEntry<K,V> evict(final ICacheEvictContext<K, V> context);

    /**
     * 更新key
     * @param key key
     */
    void updateKey(final K key);

    /**
     * 删除key
     * @param key key
     */
    void removeKey(final K key);
}
