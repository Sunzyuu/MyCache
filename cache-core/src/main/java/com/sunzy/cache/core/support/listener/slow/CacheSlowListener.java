package com.sunzy.cache.core.support.listener.slow;

import com.alibaba.fastjson.JSON;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICacheSlowListener;
import com.sunzy.cache.api.ICacheSlowListenerContext;

public class CacheSlowListener<K, V> implements ICacheSlowListener {

    private static final Log log = LogFactory.getLog(CacheSlowListener.class);
    @Override
    public void listen(ICacheSlowListenerContext context) {
        log.warn("[Slow] methodName: {}, params: {}, cost time: {}",
                context.methodName(), JSON.toJSON(context.params()), context.costTimeMills());
    }

}
