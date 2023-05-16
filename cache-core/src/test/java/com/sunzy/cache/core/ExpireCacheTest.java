package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;

public class ExpireCacheTest {

    public static void main(String[] args) throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .evict(CacheEvicts.fifo())
                .build();

        cache.put("1", "1");
        cache.put("2", "2");

        cache.expire("1", 1000);

        Thread.sleep(100);
        System.out.println(cache.keySet());
        Thread.sleep(1000);
        System.out.println(cache.keySet());
    }
}
