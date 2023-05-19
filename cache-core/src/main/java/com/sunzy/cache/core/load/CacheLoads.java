package com.sunzy.cache.core.load;

import com.sunzy.cache.api.ICacheLoad;

public class CacheLoads<K, V> {

    /**
     * 无加载
     * @param <K> key
     * @param <V> value
     * @return 值
     * @since 0.0.7
     */
    public static <K,V> ICacheLoad<K,V> none() {
        return new CacheLoadNone<>();
    }

    /**
     * aof加载
     * @param dbPath
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheLoad<K,V> aof(String dbPath) {
        return new CacheLoadAof<>(dbPath);
    }

    /**
     * rdb 文件加载
     * @param dbPath
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> ICacheLoad<K,V> json(String dbPath) {
        return new CacheLoadJSON<>(dbPath);
    }
}
