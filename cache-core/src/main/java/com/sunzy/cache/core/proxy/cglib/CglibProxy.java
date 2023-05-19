package com.sunzy.cache.core.proxy.cglib;

import com.sunzy.cache.api.ICache;
import com.sunzy.cache.core.proxy.ICacheProxy;
import com.sunzy.cache.core.proxy.bs.CacheProxyBs;
import com.sunzy.cache.core.proxy.bs.CacheProxyBsContext;
import com.sunzy.cache.core.proxy.bs.ICacheProxyBsContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor, ICacheProxy {


    /**
     * 被代理的对象
     */
    private final ICache target;

    public CglibProxy(ICache target) {
        this.target = target;
    }


    @Override
    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        // 设置目标对象类
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        //通过字节码技术创建目标对象类的子类实例作为代理
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        ICacheProxyBsContext context = CacheProxyBsContext.newInstance()
                .method(method).params(params).target(target);
        // 通过引导增强器类在原方法基础上 执行一些额外的操作
        return CacheProxyBs.newInstance().context(context).execute();
    }
}
