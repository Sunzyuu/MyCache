package com.sunzy.cache.core.evict;

import com.sunzy.cache.api.ICacheEvict;

/**
 * 用于创建不同的驱逐策略
 */
public class CacheEvicts {

    private CacheEvicts(){}

    /**
     * 默认无策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V> none() {
        return new CacheEvictNone<>();
    }


    /**
     * 先进先出
     * @param <K> key
     * @param <V> value
     * @return 结果
     */
    public static <K, V> ICacheEvict<K, V> fifo() {
        return new CacheEvictFIFO<>();
    }


    /**
     * lru驱逐策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V>lru() {
        return new CacheEvictLRU<>();
    }

    /**
     * lrun基于双向链表的驱逐策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V>doubleListLru() {
        return new CacheEvictLruDoubleListMap<>();
    }


    /**
     * lru基于LinkedHashMap的驱逐策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V>linkedListHashMapLru() {
        return new CacheEvictLruLinkedHashMap<>();
    }


    /**
     * lru基于two queue的驱逐策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V> cacheEvict2Q() {
        return new CacheEvictLru2Q<>();
    }


    /**
     * lru2 驱逐策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V> cacheEvictLru2() {
        return new CacheEvictLru2<>();
    }

    /**
     * lfu驱逐策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V> cacheEvictLfu(){
        return new CacheEvictLfu<>();
    }

    /**
     * lfu驱逐策略
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ICacheEvict<K, V> cacheEvictClock(){
        return new CacheEvictClock<>();
    }
}
