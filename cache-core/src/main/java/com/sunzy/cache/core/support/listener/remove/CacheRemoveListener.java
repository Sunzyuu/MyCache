package com.sunzy.cache.core.support.listener.remove;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICacheRemoveListener;
import com.sunzy.cache.api.ICacheRemoveListenerContext;

public class CacheRemoveListener<K, V> implements ICacheRemoveListener<K, V>{

    private static final Log log = LogFactory.getLog(CacheRemoveListener.class);
    @Override
    public void listen(ICacheRemoveListenerContext<K, V> context) {
        log.debug("Remove key: {}, value: {}, type: {}",
                context.key(), context.value(), context.type());
    }
}
