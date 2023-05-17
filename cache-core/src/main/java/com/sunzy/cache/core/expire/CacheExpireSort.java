package com.sunzy.cache.core.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.heaven.util.util.MapUtil;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheExpire;

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
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 1, 1, TimeUnit.SECONDS);
    }

    private class ExpireThread implements Runnable {

        @Override
        public void run() {
            // 如果sortMap为空，说明
            if(MapUtil.isEmpty(sortMap)){
                return;
            }

            // 获取key并处理
            int count = 0;
            for (Map.Entry<Long, List<K>> entry : sortMap.entrySet()) {
                final Long expireAt = entry.getKey();
                List<K> expireKeys = entry.getValue();
                //判断集合是否为空
                if(CollectionUtil.isEmpty(expireKeys)){
                    sortMap.remove(expireAt);
                    continue;
                }
                if(count > LIMIT){
                    return;
                }
                long currentTimeMillis = System.currentTimeMillis();
                if(currentTimeMillis >= expireAt){
                    Iterator<K> iterator = expireKeys.iterator();
                    while(iterator.hasNext()){
                        K key = iterator.next();
                        // 先移除集合中元素
                        iterator.remove();
                        // 删除过期map中的元素
                        expireMap.remove(key);
                        // 删除缓存中元素
                        cache.remove(key);
                        count++;
                    }
                } else {
                    //没有过期信息 直接结束本次任务
                    return;
                }
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        List<K> keys = sortMap.get(expireAt);
        if (keys == null) {
            keys = new ArrayList<>();
        }
        keys.add(key);

        sortMap.put(expireAt, keys);
        expireMap.put(key, expireAt);
    }



    @Override
    public void refreshExpired(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)){
            return;
        }
        // 判断大小，小的作为外循环。一般都是过期的 keys 比较小。
        if(keyList.size() <= expireMap.size()){
            for (K key : keyList) {
                Long expireAt = expireMap.get(key);
                expireKey(key, expireAt);
            }
        } else {
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.expireKey(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }


    private void expireKey(final K key, final Long expireAt) {
        if(expireAt == null){
            return;
        }
        long currentTime = System.currentTimeMillis();
        // 当前时间超过了指定过期时间
        if(currentTime >= expireAt){
            expireMap.remove(key);
            // 再移除缓冲的数据
            cache.remove(key);

            // todo:添加淘汰监听器
        }
    }
}
