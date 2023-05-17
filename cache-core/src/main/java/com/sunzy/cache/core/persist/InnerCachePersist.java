package com.sunzy.cache.core.persist;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICachePersist;
import jdk.jfr.events.ExceptionThrownEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InnerCachePersist<K, V>{
    private static final Log log = LogFactory.getLog(InnerCachePersist.class);


    private final ICache<K, V> cache;

    private final ICachePersist<K, V> persist;


    /**
     * 线程执行类
     * @since 0.0.3
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();


    public InnerCachePersist(ICache<K, V> cache, ICachePersist<K, V> persist) {
        this.cache = cache;
        this.persist = persist;
        init();
    }

    private void init(){
        EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("开始持久化缓存信息");
                    persist.persist(cache);
                    log.info("完成持久化缓存信息");
                } catch (Exception e) {
                    log.error("文件持久化异常", e);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
