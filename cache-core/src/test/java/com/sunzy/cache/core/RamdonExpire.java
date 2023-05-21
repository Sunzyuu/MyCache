package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;

public class RamdonExpire {
    public static void main(String[] args) {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.cacheEvictClock())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        cache.expire("A", 1000);
        cache.expire("B", 10000);
        cache.get("A");
        cache.get("B");
        cache.put("D", "LRU");
        cache.put("E", "LRU");
        System.out.println(cache.keySet());


    }
}
