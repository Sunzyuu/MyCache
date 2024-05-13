package com.sunzy.cache.api;

public interface ICacheEntry<K, V> {
    /**
     * @return key
     */
    K key();

    /**
     * @return value
     */
    V value();
}
