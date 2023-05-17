package com.sunzy.cache.api;

import java.lang.reflect.Method;

/**
 * 拦截器上下文接口
 * @param <K>
 * @param <V>
 */
public interface ICacheInterceptorContext<K, V>{

    /**
     * 缓存信息
     * @return 缓存信息
     * @since 0.0.5
     */
    ICache<K,V> cache();

    /**
     * 执行的方法信息
     * @return 方法
     * @since 0.0.5
     */
    Method method();

    /**
     * 执行的参数
     * @return 参数
     * @since 0.0.5
     */
    Object[] params();

    /**
     * 方法执行的结果
     * @return 结果
     * @since 0.0.5
     */
    Object result();

    /**
     * 开始时间
     * @return 时间
     * @since 0.0.5
     */
    long startMills();

    /**
     * 结束时间
     * @return 时间
     * @since 0.0.5
     */
    long endMills();
}
