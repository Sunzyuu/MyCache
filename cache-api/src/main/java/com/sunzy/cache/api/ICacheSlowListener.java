package com.sunzy.cache.api;

public interface ICacheSlowListener {
    void listen(final ICacheSlowListenerContext context);

    /**
     * 慢日志的阈值
     * @return 慢日志的阈值
     */
    long slowerThanMills();

}
