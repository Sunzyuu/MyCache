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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CacheEvictLru2Q<K, V> extends AbstractCacheEvict<K, V>{

    private static final Log log = LogFactory.getLog(CacheEvictLru2Q.class);

    private static final int LIMIT_QUEUE_SIZE = 1024;

    private Queue<K> firstQueue;

    private DoubleListNode<K,V> head;

    private DoubleListNode<K,V> tail;

    private Map<K, DoubleListNode<K,V>> lruIndexMap;

    public CacheEvictLru2Q() {
        this.firstQueue = new LinkedList<>();
        this.lruIndexMap = new HashMap<>();
        this.head = new DoubleListNode<>();
        this.tail = new DoubleListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    /**
     * 优先淘汰firstQueue中的数据
     * 如果firstQueue中数据为空，则淘汰lruMap中的数据信息
     *  这里认为被多次访问的数据，重要性高于被只访问了一次的数据
     * @param context 上下文
     * @return
     */
    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        ICache<K, V> cache = context.cache();

        if(cache.size() >= context.size()){
            K evictKey = null;
            // 1.firstQueue 不为空，优先移除队列中的元素
            if(!firstQueue.isEmpty()){
                evictKey = firstQueue.remove();
            } else {
                DoubleListNode<K, V> tailPre = this.tail.pre();
                if(tailPre == this.head){
                    log.error("当前列表为空，无法进行删除");
                    throw new RuntimeException("无法删除头节点");
                }
                evictKey = tailPre.key();
            }
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }
        return result;
    }


    @Override
    public void updateKey(K key) {
        DoubleListNode<K, V> node = lruIndexMap.get(key);
        if(ObjectUtil.isNotNull(node) || firstQueue.contains(key)){
            // 删除信息
            this.removeKey(key);
            this.addToLruMapHead(key);
            return;
        }
        // 避免第一次访问的列表一直增长，移除队头的元素
        if(firstQueue.size() >= LIMIT_QUEUE_SIZE){
            firstQueue.remove();
        }
        // 直接加入到firstQueue的队尾
        firstQueue.add(key);
    }

    /**
     * 插入LRU Map 头部
     * @param key
     */
    private void addToLruMapHead(K key){
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
        lruIndexMap.put(key, newNode);
    }

    /**
     * 移除元素
     *
     * 1. 获取 map 中的元素
     * 2. 不存在直接返回，存在执行以下步骤：
     * 2.1 删除双向链表中的元素
     * 2.2 删除 map 中的元素
     * @param key
     */
    @Override
    public void removeKey(K key) {
        DoubleListNode<K, V> node = lruIndexMap.get(key);
        if(ObjectUtil.isNotNull(node)){
            DoubleListNode<K, V> pre = node.pre();
            DoubleListNode<K, V> next = node.next();
            pre.next(next);
            next.pre(pre);
            this.lruIndexMap.remove(key);
        } else {
            firstQueue.remove(key);
        }
    }
}
