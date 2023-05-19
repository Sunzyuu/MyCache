package com.sunzy.cache.core.support.interceptor.aof;

import com.alibaba.fastjson.JSON;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheInterceptor;
import com.sunzy.cache.api.ICacheInterceptorContext;
import com.sunzy.cache.api.ICachePersist;
import com.sunzy.cache.core.model.PersistAofEntry;
import com.sunzy.cache.core.persist.CachePersistAOF;

public class CacheInterceptorAof<K, V> implements ICacheInterceptor<K, V> {

    private static final Log log = LogFactory.getLog(CacheInterceptorAof.class);

    @Override
    public void before(ICacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        ICache<K, V> cache = context.cache();
        ICachePersist<K, V> persist = cache.persist();
        if(persist instanceof CachePersistAOF){
            CachePersistAOF<K,V> cachePersistAof = (CachePersistAOF<K,V>) persist;
            String methodName = context.method().getName();
            PersistAofEntry persistAofEntry =  PersistAofEntry.newInstance();
            persistAofEntry.setMethodName(methodName);
            persistAofEntry.setParams(context.params());

            String json = JSON.toJSONString(persistAofEntry);
            log.debug("AOF 开始追加文件内容：{}", json);
            // 持久化操作
            cachePersistAof.append(json);
            log.debug("AOF 完成追加文件内容：{}", json);
        }

    }
}
