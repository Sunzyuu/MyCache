package com.sunzy.cache.core.support.interceptor.common;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICacheInterceptor;
import com.sunzy.cache.api.ICacheInterceptorContext;
import com.sunzy.cache.api.ICacheSlowListener;
import com.sunzy.cache.core.support.listener.slow.CacheSlowListenerContext;

import java.util.List;

public class CacheInterceptorCost<K, V> implements ICacheInterceptor<K, V> {
    private static final Log log = LogFactory.getLog(CacheInterceptorCost.class);

    @Override
    public void before(ICacheInterceptorContext<K, V> context) {
        log.debug("Cost start, method: {}", context.method().getName());
    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        long costMills = context.endMills() - context.startMills();
        String methodName = context.method().getName();
        log.debug("Cost end, method: {}, cost: {}ms", methodName, costMills);

        // 添加慢操作日志
        List<ICacheSlowListener> slowListenerList = context.cache().slowListeners();
        if (CollectionUtil.isNotEmpty(slowListenerList)){
            CacheSlowListenerContext listenerContext = CacheSlowListenerContext.newInstance()
                    .startTimeMills(context.startMills())
                    .endTimeMills(context.endMills())
                    .costTimeMills(costMills)
                    .methodName(methodName)
                    .params(context.params())
                    .result(context.result());
            for (ICacheSlowListener slowListener : slowListenerList) {
                // 超过慢日志的阈值 则认定为满操作
                if(costMills >= slowListener.slowerThanMills()){
                    slowListener.listen(listenerContext);
                }
            }
        }

    }
}
