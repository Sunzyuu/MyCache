package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;
import com.sunzy.cache.core.load.CacheLoads;
import com.sunzy.cache.core.support.listener.slow.CacheSlowListener;

import java.util.ArrayList;

public class ProxyTest {
    public static void main(String[] args) throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(6)
                .load(CacheLoads.none())
                .evict(CacheEvicts.fifo())
                .build();

        cache.put("1", "1");
        cache.put("3", "3");
        cache.put("2", "2");
        cache.put("4", "4");
        cache.put("5", "5");
        cache.expire("5", 11002);
        cache.expire("3", 1000);
        cache.expire("4", 1001);
        cache.expire("1", 999);
        cache.expire("2", 992);
        System.out.println(cache.keySet());
        System.out.println(cache.get("2"));
        System.out.println(cache.get("3"));

        Thread.sleep(5000000);
    }
}
