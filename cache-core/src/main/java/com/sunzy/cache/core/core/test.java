package com.sunzy.cache.core.core;

import com.sunzy.cache.annotation.CacheInterceptor;

import java.lang.reflect.Method;

public class test {
    public static void main(String[] args) throws ClassNotFoundException {
//        ICache<String, String> cache = CacheBs.<String,String>newInstance()
//                .size(2)
//                .evict(CacheEvicts.fifo())
//                .build();

        Class<?> clazz = Class.forName("com.sunzy.cache.core.core.Cache");
        Method[] methods = clazz.getMethods();
        if(methods != null);
        for (Method method : methods) {
            CacheInterceptor annotation = method.getAnnotation(CacheInterceptor.class);
            System.out.println(annotation);
            System.out.println(method.getName() + ":" + method.isAnnotationPresent(CacheInterceptor.class));
        }
    }
}
