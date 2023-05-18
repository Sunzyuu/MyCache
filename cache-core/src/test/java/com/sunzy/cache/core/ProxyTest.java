package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;
import com.sunzy.cache.core.load.CacheLoads;
import com.sunzy.cache.core.support.listener.slow.CacheSlowListener;

public class ProxyTest {
    public static void main(String[] args) throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .load(CacheLoads.none())
                .evict(CacheEvicts.fifo())
                .addSlowListener(new CacheSlowListener<>())
                .build();

        cache.put("1", "1");
        cache.put("3", "3");
        cache.put("2", "2");
        cache.expire("3", 1000);
        System.out.println(cache.keySet());
        Thread.sleep(2000);
        System.out.println(cache.get("2"));
        System.out.println(cache.get("3"));
    }
}
