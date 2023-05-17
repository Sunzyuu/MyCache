package com.sunzy.cache.core.bs;

import com.github.houbb.heaven.util.common.ArgUtil;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEvict;
import com.sunzy.cache.api.ICacheLoad;
import com.sunzy.cache.api.ICachePersist;
import com.sunzy.cache.core.core.Cache;
import com.sunzy.cache.core.core.CacheContext;
import com.sunzy.cache.core.evict.CacheEvicts;
import com.sunzy.cache.core.load.CacheLoads;
import com.sunzy.cache.core.persist.CachePersists;

import java.util.HashMap;
import java.util.Map;

/**
 * 引导类 用于创建缓存客户端
 * @param <K>
 * @param <V>
 */
public class CacheBs<K, V>{

    private CacheBs(){}

    /**
     * 使用单例模式创建一个实例
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheBs<K, V> newInstance(){
        return new CacheBs<>();
    }


    private Map<K, V> map = new HashMap<>();

    /**
     * 大小限制
     * @since 0.0.2
     */
    private int size = Integer.MAX_VALUE;

    /**
     * 驱除策略
     * @since 0.0.2
     */
    private ICacheEvict<K,V> evict;

    /**
     * map 实现
     * @param map map
     * @return this
     * @since 0.0.2
     */
    public CacheBs<K, V> map(Map<K, V> map) {
        ArgUtil.notNull(map, "map");

        this.map = map;
        return this;
    }


    /**
     * 设置 size 信息
     * @param size size
     * @return this
     * @since 0.0.2
     */
    public CacheBs<K, V> size(int size) {
        ArgUtil.notNegative(size, "size");

        this.size = size;
        return this;
    }

    /**
     * 设置驱除策略
     * @param evict 驱除策略
     * @return this
     * @since 0.0.2
     */
    public CacheBs<K, V> evict(ICacheEvict<K, V> evict) {
        this.evict = evict;
        return this;
    }

    /**
     * 加载策略 默认为nono
     * @since 0.0.7
     */
    private ICacheLoad<K,V> load = CacheLoads.none();

    /**
     * 持久化实现策略
     * @since 0.0.8
     */
    private ICachePersist<K,V> persist = CachePersists.dbJson("E:\\Sunzh\\java\\MyCache\\cache-core\\src\\main\\resources\\test.rdb");


    public CacheBs<K, V> load(ICacheLoad<K, V> load) {
        this.load = load;
        return this;
    }

    /**
     * 使用链式创建缓存客户端
     * @return
     */
    public ICache<K, V> build(){
        CacheContext<K, V> context = new CacheContext<>();
        context.cacheEvict(evict);
        context.map(map);
        context.size(size);
        return new Cache<>(context);
    }
}
