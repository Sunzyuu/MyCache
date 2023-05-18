package com.sunzy.cache.core;

import com.sunzy.cache.annotation.CacheInterceptor;
import com.sunzy.cache.core.core.Cache;

import java.lang.reflect.Method;

public class TestAnnotation {
    public static void main(String[] args) throws ClassNotFoundException {
//        ICache<String, String> cache = CacheBs.<String,String>newInstance()
//                .size(2)
//                .evict(CacheEvicts.fifo())
//                .build();

        Class<?> clazz = Class.forName(String.valueOf(Cache.class));
        Method[] methods = clazz.getMethods();
        if(methods != null);
        for (Method method : methods) {
            System.out.println(method.getName() + ":" + method.isAnnotationPresent(CacheInterceptor.class));
        }
    }
}
