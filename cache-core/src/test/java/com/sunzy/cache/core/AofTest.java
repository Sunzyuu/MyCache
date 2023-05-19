package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;
import com.sunzy.cache.core.load.CacheLoads;
import com.sunzy.cache.core.persist.CachePersists;

public class AofTest {
    public static void main(String[] args) throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
//                .persist(CachePersists.<String, String>aof("1.aof"))
//                .load(CacheLoads.aof("1.aof"))
                .evict(CacheEvicts.fifo())
                .build();

//        cache.put("1", "1");
//        cache.put("3", "3");
//        cache.put("2", "2");
//        cache.put("4", "4");
        cache.put("6", "5");
        cache.put("7", "5");
        cache.put("8", "5");

        cache.expire("7", 1000);
//        cache.expire("2", 10011);

        System.out.println(cache.keySet());
//        System.out.println(cache.get("2"));
//        System.out.println(cache.get("3"));

        Thread.sleep(5000000);
    }
}
