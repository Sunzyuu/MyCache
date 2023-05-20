package com.sunzy.cache.core.core;

import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.sunzy.cache.annotation.CacheInterceptor;
import com.sunzy.cache.api.*;
import com.sunzy.cache.core.constant.enums.CacheRemoveType;
import com.sunzy.cache.core.evict.CacheEvictContext;
import com.sunzy.cache.core.expire.CacheExpireSort;
import com.sunzy.cache.core.persist.InnerCachePersist;
import com.sunzy.cache.core.proxy.CacheProxy;
import com.sunzy.cache.core.support.listener.remove.CacheRemoveListenerContext;

import java.util.*;

public class Cache<K,V> implements ICache<K,V> {

    /**
     * map信息
     */
    private Map<K,V> map;

    /**
     * 缓存大小限制
     */
    private int sizeLimit;


    /**
     * 驱除策略
     * @since 0.0.2
     */
    private ICacheEvict<K,V> evict;

    /**
     * 过期策略
     * 暂时不做暴露
     * @since 0.0.3
     */
    private ICacheExpire<K,V> expire;

    /**
     * 慢操作日志监听类
     */
    private List<ICacheSlowListener> slowListeners;

    /**
     * 加载类
     * @since 0.0.7
     */
    private ICacheLoad<K,V> load;

    private ICachePersist<K, V> persist;

    /**
     * 删除监听类
     * @since 0.0.6
     */
    private List<ICacheRemoveListener<K,V>> removeListeners;


    @Override
    public ICachePersist<K, V> persist() {
        return this.persist;
    }

    @Override
    public ICacheEvict<K, V> evict() {
        return this.evict;
    }


    /**
     * 设置持久化策略
     * @param persist 持久化
     * @since 0.0.8
     */
    public void persist(ICachePersist<K, V> persist) {
        this.persist = persist;
    }

    /**
     * 设置驱除策略
     * @param cacheEvict 驱除策略
     * @return this
     * @since 0.0.8
     */
    public Cache<K, V> evict(ICacheEvict<K, V> cacheEvict) {
        this.evict = cacheEvict;
        return this;
    }

    public void init() {
        // 初始化过期策略
//        this.expire = new CacheExpire<>(this);
        // 使用优化后的过期策略
        this.expire = new CacheExpireSort<>(this);

        // 加载磁盘数据
        this.load.load(this);

        if(persist != null){
            new InnerCachePersist<K, V>(this, persist);
        }
    }

    public Cache() {}

    public Cache(Map<K, V> map, int sizeLimit, ICacheEvict<K, V> evict) {
        this.map = map;
        this.sizeLimit = sizeLimit;
        this.evict = evict;
    }


    @Override
    public ICacheLoad<K, V> load() {
        return load;
    }

    public Cache<K, V> load(ICacheLoad<K, V> load) {
        this.load = load;
        return this;
    }

    /**
     * 设置 map 实现
     * @param map 实现
     * @return this
     */
    public Cache<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    /**
     * 设置大小限制
     * @param sizeLimit 大小限制
     * @return this
     */
    public Cache<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        return this;
    }

    /**
     * 是否已经达到大小最大的限制
     * @return 是否限制
     * @since 0.0.2
     */
    private boolean isSizeLimit() {
        final int currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }


    /**
     * 为了便于处理，我们将多久之后过期，进行计算。
     * 将两个问题变成同一个问题，在什么时候过期的问题。
     * @param key         key
     * @param timeInMills 毫秒时间之后过期
     * @return
     */
    @Override
    @CacheInterceptor
    public ICache<K, V> expire(K key, long timeInMills) {
        long expireTime = System.currentTimeMillis() + timeInMills;
        // 使用代理调用
        Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(this);
        return cachePoxy.expireAt(key, expireTime);
    }

    @Override
    @CacheInterceptor(aof = true)
    public ICache<K, V> expireAt(K key, long timeInMills) {
        this.expire.expire(key, timeInMills);
        return this;
    }

    @Override
    @CacheInterceptor
    public ICacheExpire<K, V> expire() {
        return this.expire;
    }

    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return removeListeners;
    }

    @Override
    public List<ICacheSlowListener> slowListeners() {
        return slowListeners;
    }

    public Cache<K, V> slowListeners(List<ICacheSlowListener> listeners) {
        this.slowListeners = listeners;
        return this;
    }

    public Cache<K, V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners) {
        this.removeListeners = removeListeners;
        return this;
    }

    @Override
    @CacheInterceptor(refresh = true)
    public int size() {
        return map.size();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    @CacheInterceptor(refresh = true, evict = true)
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }


    @Override
    @CacheInterceptor(evict = true)
    public V get(Object key) {
        // 为了保证数据的可用性，在获取之前刷新过期时间
        //1. 刷新所有过期信息
        K genericKey = (K) key;
        this.expire.refreshExpired(Collections.singletonList(genericKey));
        return map.get(key);
    }

    /**
     * 重写hashMap的put方法，增加了驱逐策略，会在添加元素之前进行检查
     * @param key
     * @param value
     * @return
     */
    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V put(K key, V value) {
        //1.1 尝试驱除
        CacheEvictContext<K,V> context = new CacheEvictContext<>();
        context.key(key).size(sizeLimit).cache(this);
        ICacheEntry<K, V> evictEntry = evict.evict(context);

        // 添加拦截器调用
        if(ObjectUtil.isNotNull(evictEntry)){
            // 执行淘汰监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(evictEntry.key())
                    .value(evictEntry.value())
                    .type(CacheRemoveType.EVICT.code());
            for(ICacheRemoveListener<K,V> listener : context.cache().removeListeners()) {
                listener.listen(removeListenerContext);
            }
        }


        //2. 判断驱除后的信息
        if(isSizeLimit()) {
            throw new RuntimeException("当前队列已满，数据添加失败！");
        }
        //3. 执行添加
        return map.put(key, value);
    }



    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    @CacheInterceptor(aof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(refresh = true, aof = true)
    public void clear() {
        map.clear();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
