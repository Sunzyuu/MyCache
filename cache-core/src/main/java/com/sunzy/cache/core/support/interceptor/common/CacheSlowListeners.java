package com.sunzy.cache.core.support.interceptor.common;

import com.sunzy.cache.api.ICacheSlowListener;
import com.sunzy.cache.core.support.listener.slow.CacheSlowListener;

import java.util.ArrayList;
import java.util.List;

public class CacheSlowListeners {
    public static List<ICacheSlowListener> none(){
        return new ArrayList<>();
    }


    /**
     * 默认实现
     * @return 默认
     */
    public static  List<ICacheSlowListener> defaults() {
        List<ICacheSlowListener> slowListeners = new ArrayList<>();
        ICacheSlowListener slowListener = new CacheSlowListener<>();
        slowListeners.add(slowListener);
        return slowListeners;
    }
}
