package com.sunzy.cache.core.model;

public class CircleListNode<K, V> {

    private K key;

    private V value = null;

    private boolean accessFlag = false;


    private CircleListNode<K, V> pre;

    private CircleListNode<K, V> next;

    public CircleListNode(K key) {
        this.key = key;
    }

    public K key() {
        return key;
    }

    public CircleListNode<K, V> key(K key) {
        this.key = key;
        return this;
    }


    public CircleListNode<K, V> value(V value) {
        this.value = value;
        return this;
    }

    public V value() {return value;}

    public boolean accessFlag() {
        return accessFlag;
    }

    public CircleListNode<K, V> accessFlag(boolean accessFlag) {
        this.accessFlag = accessFlag;
        return this;
    }

    public CircleListNode<K, V> pre() {
        return pre;
    }

    public CircleListNode<K, V> pre(CircleListNode<K, V> pre) {
        this.pre = pre;
        return this;
    }

    public CircleListNode<K, V> next() {
        return next;
    }

    public CircleListNode<K, V> next(CircleListNode<K, V> next) {
        this.next = next;
        return this;
    }

    @Override
    public String toString() {
        return "CircleListNode{" +
                "key=" + key +
                ", value=" + value +
                ", accessFlag=" + accessFlag +
                ", pre=" + pre +
                ", next=" + next +
                '}';
    }

}
