package com.sunzy.cache.core.proxy.bs;

import com.sunzy.cache.annotation.CacheInterceptor;
import com.sunzy.cache.api.ICache;

import java.lang.reflect.Method;

public class CacheProxyBsContext implements ICacheProxyBsContext {

    /**
     * 代理目标对象
     * @since 0.0.4
     */
    private ICache target;

    /**
     * 方法执行的参数
     * @since 0.0.4
     */
    private Object[] params;

    /**
     * 方法
     * @since 0.0.4
     */
    private Method method;

    /**
     * 注解拦截器
     * @since 0.0.5
     */
    private CacheInterceptor interceptor;

    /**
     * 新建对象
     * @return 对象
     * @since 0.0.4
     */
    public static CacheProxyBsContext newInstance(){
        return new CacheProxyBsContext();
    }

    @Override
    public CacheInterceptor interceptor() {
        return interceptor;
    }

    @Override
    public ICache target() {
        return target;
    }

    @Override
    public ICacheProxyBsContext target(ICache target) {
        this.target = target;
        return this;
    }

    @Override
    public Object[] params() {
        return  params;
    }

    public CacheProxyBsContext params(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public Method method() {
        return method;
    }

    public CacheProxyBsContext method(Method method) {
        this.method = method;
        // 获取注释对象
        this.interceptor = method.getAnnotation(CacheInterceptor.class);
        return this;
    }

    @Override
    public Object process() throws Throwable {
        //通过反射调用方法
        return this.method.invoke(target, params);
    }
}
