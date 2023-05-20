package com.sunzy.cache.core.evict;

import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.api.ICacheEvictContext;
import com.sunzy.cache.core.common.CacheEntry;
import com.sunzy.cache.core.model.DoubleListNode;

import java.util.HashMap;
import java.util.Map;

public class CacheEvictLruDoubleListMap<K, V> extends AbstractCacheEvict<K, V>{

    private static final Log log = LogFactory.getLog(CacheEvictLruDoubleListMap.class);

    /**
     * 头节点
     */
    private DoubleListNode<K, V> head;

    /**
     * 尾节点
     */
    private DoubleListNode<K, V> tail;

    /**
     * map 信息
     *
     * key: 元素信息
     * value: 元素在 list 中对应的节点信息
     */
    private Map<K, DoubleListNode<K, V>> indexMap;

    public CacheEvictLruDoubleListMap() {
        this.indexMap = new HashMap<>();
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        ICache<K, V> cache = context.cache();
        // 超出限制 移除队尾元素
        if(cache.size() >= context.size()){
            // 获取队尾元素的前一个元素
            DoubleListNode<K, V> tailPre = this.tail.pre();
            if(tailPre == this.head){
                log.error("当前列表为空，无法进行删除");
                throw new RuntimeException("不可删除头结点");
            }

            K evictKey = tailPre.key();
            V removeValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, removeValue);
        }

        return result;
    }

    /**
     * 放入元素
     *  （1） 删除已经存在的元素
     *  （2） 新元素放到头部
     * @param key
     */
    @Override
    public void updateKey(K key) {
        // 先删除元素
        this.removeKey(key);
        DoubleListNode<K, V> newNode = new DoubleListNode<>();
        newNode.key(key);


        DoubleListNode<K, V> next = this.head.next();
        this.head.next(newNode);
        newNode.pre(this.head);
        next.pre(newNode);
        newNode.next(next);

        //将节点信息插入到map中
        indexMap.put(key, newNode);

    }

    /**
     * 移除元素
     *
     * 1. 获取 map 中的元素
     * 2. 不存在直接返回，存在执行以下步骤：
     * 2.1 删除双向链表中的元素
     * 2.2 删除 map 中的元素
     *
     * @param key
     */
    @Override
    public void removeKey(K key) {
        DoubleListNode<K, V> node = indexMap.get(key);
        if(ObjectUtil.isEmpty(node)){
            return;
        }

        DoubleListNode<K, V> pre = node.pre();
        DoubleListNode<K, V> next = node.next();

        // 从链表中摘除该节点
        pre.next(next);
        next.pre(pre);

        // 删除map中node节点
        this.indexMap.remove(key);
    }
}
