package com.sunzy.cache.api;

public interface ICacheEvictContext<K, V> {

    K key();

    ICache<K, V > cache();

    int size();
}
