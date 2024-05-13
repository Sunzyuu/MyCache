package com.sunzy.cache.api;

import java.util.Map;

public interface ICacheContext <K, V>{
    /**
     * map 信息
     * @return map
     */
    Map<K, V> map();

    /**
     * 大小限制
     * @return 大小限制
     */
    int size();

    /**
     * 驱除策略
     * @return 策略
     */
    ICacheEvict<K,V> cacheEvict();

}
