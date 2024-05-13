package com.sunzy.cache.api;

/**
 * 拦截器接口
 *
 * （1）耗时统计
 * （2）监听器
 *
 * @author binbin.hou
 * @param <K> key
 * @param <V> value
 */
public interface ICacheInterceptor<K,V> {

    /**
     * 方法执行之前
     * @param context 上下文
     */
    void before(ICacheInterceptorContext<K,V> context);

    /**
     * 方法执行之后
     * @param context 上下文
     */
    void after(ICacheInterceptorContext<K,V> context);

}
