package com.sunzy.cache.core.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.heaven.util.util.MapUtil;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheExpire;
import com.sunzy.cache.api.ICacheRemoveListener;
import com.sunzy.cache.api.ICacheRemoveListenerContext;
import com.sunzy.cache.core.constant.enums.CacheRemoveType;
import com.sunzy.cache.core.support.listener.remove.CacheRemoveListenerContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 缓存过期清理策略
 * @param <K>
 * @param <V>
 */
public class CacheExpire<K, V> implements ICacheExpire<K, V> {
    /**
     * 单次清空数量限制
     */
    private static final int LIMIT = 100;

    /**
     * 存过key以及对应的过期时间
     */
    private final Map<K, Long> expireMap = new HashMap<>();

    private final ICache<K, V> cache;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpire(ICache<K, V> cache) {
        this.cache = cache;
        // 初始化定时任务，实现每100ms删除一次
        init();
    }

    /**
     * 初始化定时任务线程信息
     */
    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    private class ExpireThread implements Runnable {

        @Override
        public void run() {
            if(MapUtil.isEmpty(expireMap)){
                return;
            }

            // 获取key并处理
            int count = 0;
            for (Map.Entry<K, Long> entry : expireMap.entrySet()) {
                if(count > LIMIT){
                    return;
                }
                // 交给过期函数进行处理
                expireKey(entry.getKey(), entry.getValue());
                count++;
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
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
        return null;
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
            V removeValue = cache.remove(key);

            // todo:添加淘汰监听器
//            ICacheRemoveListenerContext<K, V> listenerContext = CacheRemoveListenerContext.<K, V>newInstance().
//                    key(key).
//                    value(removeValue).
//                    type(CacheRemoveType.EXPIRE.code());
//            for (ICacheRemoveListener<K, V> listener : cache.removeListeners()) {
//                listener.listen(listenerContext);
//            }
        }
    }
}
