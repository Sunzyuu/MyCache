package com.sunzy.cache.core;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.bs.CacheBs;
import com.sunzy.cache.core.evict.CacheEvicts;

public class test {

    public static void main(String[] args) {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .evict(CacheEvicts.fifo())
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");
        System.out.println(cache.size());
        System.out.println(cache.keySet());


    }
}
