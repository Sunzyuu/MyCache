package com.sunzy.cache.core.evict;

import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvictContext;
import com.sunzy.cache.core.common.CacheEntry;
import com.sunzy.cache.core.model.FreqNode;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class CacheEvictLfu<K, V> extends AbstractCacheEvict<K, V>{

    private static Log log = LogFactory.getLog(CacheEvictLfu.class);

    /**
     * key 映射信息
     */
    private final Map<K, FreqNode<K, V>> keyMap;

    /**
     * 频率 map
     * 就是下面的结构
     * 1 : FreqNode(1) -> FreqNode(2)
     * 2 : FreqNode(3) -> FreqNode(4)
     */
    private final Map<Integer, LinkedHashSet<FreqNode<K,V>>> freqMap;

    /**
     * 元素的最小使用频率
     */
    private int minFreq;

    public CacheEvictLfu() {
        this.keyMap = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.minFreq = 1;
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        ICache<K, V> cache = context.cache();
        if(cache.size() >= context.size()){
            FreqNode<K, V> evictNode = this.getMinFreqNode();
            K evictKey = evictNode.key();
            V evictValue = cache.remove(evictKey);
            log.debug("淘汰最小频率信息, key: {}, value: {}, freq: {}",
                    evictKey, evictValue, evictNode.frequency());
            removeKey(evictKey);
            result = new CacheEntry<K, V>(evictKey, evictValue);
        }
        return result;
    }

    private FreqNode<K, V> getMinFreqNode() {
        LinkedHashSet<FreqNode<K, V>> set = freqMap.get(minFreq);
        if(CollectionUtil.isNotEmpty(set)){
            return set.iterator().next();
        }

        throw new RuntimeException("未发现最小频率的 Key");

    }

    @Override
    public void updateKey(K key) {
        FreqNode<K, V> freqNode = keyMap.get(key);

        if(ObjectUtil.isNotNull(freqNode)) {
            // 移除原始的节点信息
            int frequency = freqNode.frequency();
            LinkedHashSet<FreqNode<K, V>> oldSet = freqMap.get(frequency);
            // 移除头节点
            oldSet.remove(freqNode);

            // 如果frequency 与 minFreq相同，且其中所有的节点都被移除后，说明频率为minFreq的节点没有了，minFreq需要更新
            if (minFreq == frequency && oldSet.isEmpty()) {
                minFreq++;
                log.debug("minFreq 增加为：{}", minFreq);
            }

            frequency++;
            freqNode.frequency(frequency);
            // 放入新的集合
            this.addToFreqMap(frequency, freqNode);
        } else {
            // 构建新的匀速
            FreqNode<K, V> newNode = new FreqNode<>(key);

            // 固定放入频率为1的列表中
            this.addToFreqMap(1, newNode);
            // 更新minFreq
            this.minFreq = 1;
            // 添加到keyMap
            this.keyMap.put(key, newNode);
        }

    }

    private void addToFreqMap(int frequency, FreqNode<K, V> freqNode) {
        LinkedHashSet<FreqNode<K, V>> set = freqMap.get(frequency);
        if(set == null){
            set = new LinkedHashSet<>();
        }

        set.add(freqNode);
        freqMap.put(frequency, set);
        log.debug("freq:{} 添加元素节点: {}", frequency, freqNode);
    }

    @Override
    public void removeKey(K key) {
        FreqNode<K, V> freqNode = this.keyMap.remove(key);
        // 获取key频率
        int freq = freqNode.frequency();
        LinkedHashSet<FreqNode<K, V>> set = this.freqMap.get(freq);

        // 移除频率中对应的点
        set.remove(freqNode);
        log.debug("freq={} 移除元素节点：{}", freq, freqNode);

        // 更新 minFreq
        if(CollectionUtil.isEmpty(set) && minFreq == freq){
            minFreq --;
            log.debug("minFreq 降低为：{}", minFreq);
        }
    }


}
