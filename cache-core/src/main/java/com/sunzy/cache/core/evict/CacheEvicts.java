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


}