package com.sunzy.cache.core.support.struct.lru.impl;

import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.core.common.CacheEntry;
import com.sunzy.cache.core.model.DoubleListNode;
import com.sunzy.cache.core.support.struct.lru.ILruMap;

import java.util.HashMap;
import java.util.Map;

public class LruMapDoubleList<K, V> implements ILruMap<K, V> {
    private static final Log log = LogFactory.getLog(LruMapDoubleList.class);

    private DoubleListNode<K, V> head;

    private DoubleListNode<K, V> tail;

    private Map<K, DoubleListNode<K, V>> indexMap;

    public LruMapDoubleList() {
        this.indexMap = new HashMap<>();
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    public ICacheEntry<K, V> removeEldest() {
        // 获取尾巴节点的前一个元素
        DoubleListNode<K, V> tailPre = this.tail.pre();
        if(tailPre == this.head){
            log.error("无法删除头节点");
            throw new RuntimeException("不可删除头结点!");
        }
        K evictKey = tailPre.key();
        V evictValue = tailPre.value();

        // 执行删除
        this.removeKey(evictKey);
        return CacheEntry.of(evictKey, evictValue);
    }

    @Override
    public void updateKey(K key) {
        //1. 执行删除
        this.removeKey(key);

        //2. 新元素插入到头部
        //head<->next
        //变成：head<->new<->next
        DoubleListNode<K,V> newNode = new DoubleListNode<>();
        newNode.key(key);

        DoubleListNode<K,V> next = this.head.next();
        this.head.next(newNode);
        newNode.pre(this.head);
        next.pre(newNode);
        newNode.next(next);

        //2.2 插入到 map 中
        indexMap.put(key, newNode);
    }

    @Override
    public void removeKey(K key) {
        DoubleListNode<K,V> node = indexMap.get(key);

        if(ObjectUtil.isNull(node)) {
            return;
        }

        // 删除 list node
        // A<->B<->C
        // 删除 B，需要变成： A<->C
        DoubleListNode<K,V> pre = node.pre();
        DoubleListNode<K,V> next = node.next();

        pre.next(next);
        next.pre(pre);

        // 删除 map 中对应信息
        this.indexMap.remove(key);
        log.debug("从 LruMapDoubleList 中移除 key: {}", key);
    }

    @Override
    public boolean isEmpty() {
        return indexMap.isEmpty();
    }

    @Override
    public boolean contains(K key) {
        return indexMap.containsKey(key);
    }
}
