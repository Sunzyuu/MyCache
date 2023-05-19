package com.sunzy.cache.core.support.interceptor;

import com.sunzy.cache.api.ICacheInterceptor;
import com.sunzy.cache.core.support.interceptor.aof.CacheInterceptorAof;
import com.sunzy.cache.core.support.interceptor.common.CacheInterceptorCost;

import java.util.ArrayList;
import java.util.List;


/**
 * 缓存拦截器工具类
 */
public class CacheInterceptors {

    /**
     * 默认通用
     * @return 结果
     * @since 0.0.5
     */
    @SuppressWarnings("all")
    public static List<ICacheInterceptor> defaultCommonList() {
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorCost());
        return list;
    }

    /**
     * 默认刷新
     * @return 结果
     * @since 0.0.5
     */
    @SuppressWarnings("all")
    public static List<ICacheInterceptor> defaultRefreshList() {
        List<ICacheInterceptor> list = new ArrayList<>();
//        list.add(new CacheInterceptorRefresh());
        return list;
    }

    @SuppressWarnings("all")
    public static ICacheInterceptor aof() {
        return new CacheInterceptorAof();
    }
}
