package com.sunzy.cache.core.support.struct.lru.impl;

import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICacheEntry;
import com.sunzy.cache.core.common.CacheEntry;
import com.sunzy.cache.core.model.CircleListNode;
import com.sunzy.cache.core.support.struct.lru.ILruMap;

import java.util.HashMap;
import java.util.Map;

public class LruMapCircleList<K, V> implements ILruMap<K, V> {

    private static final Log log = LogFactory.getLog(LruMapCircleList.class);


    private CircleListNode<K, V> head;

    /**
     * 映射 map
     * @since 0.0.15
     */
    private Map<K, CircleListNode<K,V>> indexMap;

    public LruMapCircleList() {
        // 双向循环链表
        this.head = new CircleListNode<>(null);
        this.head.next(this.head);
        this.head.pre(this.head);

        indexMap = new HashMap<>();
    }

    @Override
    public ICacheEntry<K, V> removeEldest() {
        if (isEmpty()) {
            log.error("当前列表为空，无法进行删除");
            throw new RuntimeException("不可删除头节点");
        }
        // 从最老的元素开始，此处直接从 head.next 开始，后续可以考虑优化记录这个 key
        CircleListNode<K, V> node = this.head;
        while (node.next() != this.head) {
            // 下一个元素
            node = node.next();

            if(!node.accessFlag()){
                K key = node.key();
                this.removeKey(key);
                return CacheEntry.of(key, node.value());
            } else {
                node.accessFlag(false);
            }
        }

        // 如果循环一遍都没找到，直接取第一个元素即可
        CircleListNode<K, V> firstNode = this.head.next();
        return CacheEntry.of(firstNode.key(), firstNode.value());
    }

    @Override
    public void updateKey(K key) {
        CircleListNode<K, V> node = indexMap.get(key);

        if(ObjectUtil.isNotNull(node)) {
            // 将访问的标志位设置为true
            node.accessFlag(true);
            log.debug("节点已存在，设置节点访问标识为 true, key: {}", key);
        } else {
            node = new CircleListNode<>(key);
            CircleListNode<K, V> tail = head.pre();
            tail.next(node);
            node.pre(tail);
            node.next(head);
            head.pre(node);

            // 放入indexMap中，便于快速定位
            indexMap.put(key, node);
            log.debug("节点不存在，新增节点到链表中: {}", key);
        }
    }

    @Override
    public void removeKey(K key) {
        CircleListNode<K, V> node = indexMap.get(key);
        if(ObjectUtil.isNull(node)){
            log.warn("对应的删除信息不存在：{}", key);
            return;
        }
        CircleListNode<K, V> pre = node.pre();
        CircleListNode<K, V> next = node.next();
        // 将该节点从中摘出来
        pre.next(next);
        next.pre(pre);
        indexMap.remove(key);
        log.debug("Key: {} 从循环链表中移除", key);
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
