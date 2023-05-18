package com.sunzy.cache.core.proxy.none;

import com.sunzy.cache.core.proxy.ICacheProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 不使用代理对象
 */
public class NoneProxy implements InvocationHandler, ICacheProxy {

    /**
     * 代理对象
     */
    private final Object target;

    public NoneProxy(Object target) {
        this.target = target;
    }


    @Override
    public Object proxy() {
        return this.target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(proxy, args);
    }
}
