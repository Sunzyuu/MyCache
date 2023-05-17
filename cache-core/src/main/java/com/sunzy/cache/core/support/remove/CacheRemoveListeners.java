package com.sunzy.cache.core.support.remove;

import com.sunzy.cache.api.ICacheRemoveListener;

import java.util.ArrayList;
import java.util.List;

public class CacheRemoveListeners{

    private CacheRemoveListeners(){}
    public  static <K,V> List<ICacheRemoveListener<K,V>> defaults() {
        List<ICacheRemoveListener<K, V>> listeners = new ArrayList<>();
        listeners.add(new CacheRemoveListener<>());
        return listeners;
    }
}
