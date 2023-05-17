package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;
import com.sunzy.cache.core.load.CacheLoadNone;

public class PersistTest {
    public static void main(String[] args) {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .evict(CacheEvicts.fifo())
                .load(new CacheLoadNone<>())
                .build();
        cache.put("1", "1");
        cache.put("2", "2");
        System.out.println(cache.size());
    }
}
