package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;

public class RemoveCacheTest {

    public static void main(String[] args) throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .evict(CacheEvicts.fifo())
                .build();

        cache.put("1", "1");
        cache.put("2", "2");

//        cache.expire("1", 1000);
//        Thread.sleep(2000);
        cache.put("3", "3");
        cache.expire("2", 100);

        System.out.println(cache.keySet());
        System.out.println(cache.keySet());
    }
}
