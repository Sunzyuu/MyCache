package com.sunzy.cache.core.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.heaven.util.util.MapUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheExpire;
import com.sunzy.cache.api.ICacheRemoveListener;
import com.sunzy.cache.api.ICacheRemoveListenerContext;
import com.sunzy.cache.core.constant.enums.CacheRemoveType;
import com.sunzy.cache.core.support.listener.remove.CacheRemoveListenerContext;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CacheExpireRandom <K, V> implements ICacheExpire<K, V> {
    private static final Log log = LogFactory.getLog(CacheExpireRandom.class);

    /**
     * 单次清空的数量限制
     * @since 0.0.16
     */
    private static final int COUNT_LIMIT = 100;

    /**
     * 过期 map
     *
     * 空间换时间
     * @since 0.0.16
     */
    private final Map<K, Long> expireMap = new HashMap<>();

    /**
     * 缓存实现
     * @since 0.0.16
     */
    private final ICache<K,V> cache;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();


    public CacheExpireRandom(ICache<K,V> cache) {
        this.cache = cache;
        init();
    }

    private void init() {
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThreadRandom(), 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 是否启用快模式
     * @since 0.0.16
     */
    private volatile boolean fastMode = false;


    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    @Override
    public void refreshExpired(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)){
            return;
        }

        if(keyList.size() <= expireMap.size()) {
            for (K key : keyList) {
                Long expireAt = expireMap.get(key);
                expireKey(key, expireAt);
            }
        } else {
            for (Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.expireKey(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    private  class ExpireThreadRandom implements Runnable {
        @Override
        public void run() {

            if(MapUtil.isEmpty(expireMap)) {
                log.info("expireMap 信息为空，直接跳过本次处理。");
                return;
            }
            if(fastMode) {
                expireKeys(10L);
            }
            expireKeys(100L);

        }
    }


    /**
     * 过期信息
     * @param timeoutMills
     */
    private void expireKeys(final long timeoutMills) {
        final long timeLimit = System.currentTimeMillis() + timeoutMills;

        this.fastMode = false;
        int count = 0;

        while(true) {
            if(count >= COUNT_LIMIT) {
                log.info("过期淘汰次数已经达到最大次数: {}，完成本次执行。", COUNT_LIMIT);
                return;
            }
            if(System.currentTimeMillis() >= timeLimit) {
                this.fastMode = true;
                log.info("过期淘汰已经达到限制时间，中断本次执行，设置 fastMode=true;");
                return;
            }
            K key = getRandomKey();
            Long expireAt = expireMap.get(key);
            boolean expireFlag = expireKey(key, expireAt);
            log.debug("key: {} 过期执行结果 {}", key, expireFlag);
            //2.3 信息更新
            count++;
        }
    }

    private boolean expireKey(K key, Long expireAt) {
        if(expireAt == null){
            return false;
        }

        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis >= expireAt){
            expireMap.remove(key);
            V removeValue = cache.remove(key);

            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext
                    .<K,V>newInstance()
                    .key(key)
                    .value(removeValue)
                    .type(CacheRemoveType.EXPIRE.code());

            for (ICacheRemoveListener<K, V> listener : cache.removeListeners()) {
                listener.listen(removeListenerContext);
            }
            return true;
        }
        return false;
    }

    private K getRandomKey() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Set<K> keySet = expireMap.keySet();
        List<K> list = new ArrayList<>(keySet);
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }


    private K getRandomKey2() {
        Random random = ThreadLocalRandom.current();
        int randomIndex = random.nextInt(expireMap.size());
        // 遍历 keys
        Iterator<K> iterator = expireMap.keySet().iterator();
        int count = 0;
        while (iterator.hasNext()) {
            K key = iterator.next();
            if(count == randomIndex) {
                return key;
            }
            count++;
        }
        // 正常逻辑不会到这里
        throw new RuntimeException("对应信息不存在");
    }
}
