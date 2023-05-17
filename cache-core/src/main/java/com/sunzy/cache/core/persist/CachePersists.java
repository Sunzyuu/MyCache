package com.sunzy.cache.core.persist;

import com.sunzy.cache.api.ICachePersist;

public class CachePersists {

    /**
     * DB json 操作
     * @param <K> key
     * @param <V> value
     * @param path 文件路径
     * @return 结果
     */
    public static <K,V> ICachePersist<K,V> dbJson(final String path) {
        return new CachePersistDbJson<>(path);
    }
}
