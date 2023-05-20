package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;

public class LRUTest {
    public static void main(String[] args) {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.lru())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        cache.get("A");
        cache.put("D", "LRU");
        System.out.println(cache.keySet());
        cache.expire("A", 1000);


    }
}
