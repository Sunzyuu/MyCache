package com.sunzy.cache.core.proxy.dynamic;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.proxy.ICacheProxy;
import com.sunzy.cache.core.proxy.bs.CacheProxyBs;
import com.sunzy.cache.core.proxy.bs.CacheProxyBsContext;
import com.sunzy.cache.core.proxy.bs.ICacheProxyBsContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements InvocationHandler, ICacheProxy {

    /**
     * 被代理的对象
     */
    private final ICache target;

    public DynamicProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object proxy() {
        InvocationHandler handler = new DynamicProxy(target);
        return Proxy.newProxyInstance(
                handler.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                handler
        );
    }

    /**
     * 这种方式虽然实现了异步执行，但是存在一个缺陷：
     * 强制用户返回值为 Future 的子类。
     *
     * 如何实现不影响原来的值，要怎么实现呢？
     * @param proxy 原始对象
     * @param method 方法
     * @param args 入参
     * @return 结果
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ICacheProxyBsContext context = CacheProxyBsContext.newInstance()
                .method(method).params(args).target(target);
        return CacheProxyBs.newInstance().context(context).execute();
    }
}
