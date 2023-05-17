package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheLoad;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;
import com.sunzy.cache.core.load.CacheLoadNone;

public class LoadTest {
    public static void main(String[] args) {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .evict(CacheEvicts.fifo())
                .load(new CacheLoadNone<>())
                .build();

        System.out.println(cache.size());
    }
}
