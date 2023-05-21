package com.sunzy.cache.core.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.heaven.util.util.MapUtil;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheExpire;
import com.sunzy.cache.api.ICacheRemoveListener;
import com.sunzy.cache.api.ICacheRemoveListenerContext;
import com.sunzy.cache.core.constant.enums.CacheRemoveType;
import com.sunzy.cache.core.support.listener.remove.CacheRemoveListenerContext;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 缓存过期清理策略优化版
 * 让过期时间作为key，相同时间的需要过期的信息放在一个列表中，作为value
 * 对过期时间进行排序，轮询时就可以快速判断出是否有过期的信息
 * @param <K>
 * @param <V>
 */
public class CacheExpireSort<K, V> implements ICacheExpire<K, V> {
    /**
     * 单次清空数量限制
     */
    private static final int LIMIT = 100;
    /**
     * 使用按照时间的缓存处理
     */
    private final Map<Long, List<K>> sortMap = new TreeMap<>((o1, o2) -> (int) (o1 - o2));

    /**
     * 存过key以及对应的过期时间
     */
    private final Map<K, Long> expireMap = new HashMap<>();

    private final ICache<K, V> cache;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpireSort(ICache<K, V> cache) {
        this.cache = cache;
        // 初始化定时任务，实现每100ms删除一次
        init();
    }

    /**
     * 初始化定时任务线程信息
     */
    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 2, 1, TimeUnit.SECONDS);
    }

    /**
     * 定时执行任务
     * @since 0.0.3
     */
    private class ExpireThread implements Runnable {
        @Override
        public void run() {
            System.out.println(sortMap.size());
            //1.判断是否为空
            if(MapUtil.isEmpty(sortMap)) {
                return;
            }

            //2. 获取 key 进行处理
            int count = 0;
            for(Map.Entry<Long, List<K>> entry : sortMap.entrySet()) {
                final Long expireAt = entry.getKey();
                List<K> expireKeys = entry.getValue();

                // 判断过期keys是否为空
                if(CollectionUtil.isEmpty(expireKeys)) {
                    sortMap.remove(expireAt);
                    continue;
                }
                if(count >= LIMIT) {
                    return;
                }

                // 删除的逻辑处理
                long currentTime = System.currentTimeMillis();
                if(currentTime >= expireAt) {
                    Iterator<K> iterator = expireKeys.iterator();
                    while (iterator.hasNext()) {
                        K key = iterator.next();
                        // 先移除本身
                        iterator.remove();
                        expireMap.remove(key);

                        // 再移除缓存，后续可以通过惰性删除做补偿
                        V evictValue = cache.remove(key);

                        ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext
                                .<K,V>newInstance()
                                .key(key)
                                .value(evictValue)
                                .type(CacheRemoveType.EXPIRE.code());
                        for(ICacheRemoveListener<K,V> listener : cache.removeListeners()) {
                            listener.listen(removeListenerContext);
                        }

                        count++;
                    }
                } else {
                    // 直接跳过，没有过期的信息
                    return;
                }
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        List<K> keys = sortMap.get(expireAt);
        if(keys == null) {
            keys = new ArrayList<>();
        }
        keys.add(key);

        // 设置对应的信息
        sortMap.put(expireAt, keys);
        expireMap.put(key, expireAt);
    }

    @Override
    public void refreshExpired(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)) {
            return;
        }

        // 这样维护两套的代价太大，后续优化，暂时不用。
        // 判断大小，小的作为外循环
        final int expireSize = expireMap.size();
        if(expireSize <= keyList.size()) {
            // 一般过期的数量都是较少的
            for(Map.Entry<K,Long> entry : expireMap.entrySet()) {
                K key = entry.getKey();

                // 这里直接执行过期处理，不再判断是否存在于集合中。
                // 因为基于集合的判断，时间复杂度为 O(n)
                this.removeExpiredKey(key);
            }
        } else {
            for(K key : keyList) {
                this.removeExpiredKey(key);
            }
        }
    }

    /**
     * 移除过期信息
     * @param key key
     */
    private void removeExpiredKey(final K key) {
        Long expireTime = expireMap.get(key);
        if(expireTime != null) {
            final long currentTime = System.currentTimeMillis();
            if(currentTime >= expireTime) {
                expireMap.remove(key);

                List<K> expireKeys = sortMap.get(expireTime);
                expireKeys.remove(key);
                sortMap.put(expireTime, expireKeys);
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }
}
