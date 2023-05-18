package com.sunzy.cache.api;

public interface ICacheSlowListener {
    void listen(final ICacheSlowListenerContext context);
}
