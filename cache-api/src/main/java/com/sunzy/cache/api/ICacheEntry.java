package com.sunzy.cache.api;

public interface ICacheEntry<K, V> {
    /**
     * @since 0.0.11
     * @return key
     */
    K key();

    /**
     * @since 0.0.11
     * @return value
     */
    V value();
}
