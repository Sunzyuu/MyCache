package com.sunzy.cache.core.proxy.bs;

import com.sunzy.cache.annotation.CacheInterceptor;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheInterceptor;
import com.sunzy.cache.api.ICachePersist;
import com.sunzy.cache.core.persist.CachePersistAOF;
import com.sunzy.cache.core.support.interceptor.CacheInterceptorContext;
import com.sunzy.cache.core.support.interceptor.CacheInterceptors;

import java.util.List;

public class CacheProxyBs {

    private CacheProxyBs(){}
    /**
     * 代理上下文
     * @since 0.0.4
     */
    private ICacheProxyBsContext context;

    /**
     * 默认通用拦截器
     *
     * JDK 的泛型擦除导致这里不能使用泛型
     * @since 0.0.5
     */
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> commonInterceptors = CacheInterceptors.defaultCommonList();


    /**
     * 默认刷新拦截器
     * @since 0.0.5
     */
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> refreshInterceptors = CacheInterceptors.defaultRefreshList();

    /**
     * 持久化监听器
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor persistInterceptor = CacheInterceptors.aof();


    /**
     * 持久化监听器
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor evictInterceptor = CacheInterceptors.evict();

    public static CacheProxyBs newInstance(){
        return new CacheProxyBs();
    }

    public CacheProxyBs context(ICacheProxyBsContext context) {
        this.context = context;
        return this;
    }

    /**
     * 该方法在每个方法执行之前会获取其执行的具体信息
     * 包括执方法名，参数，执行结果，执行结果
     * 在执行前会打印执行时间
     * 然后记录执行时间
     * 打印最后的执行时间
     * @return
     * @throws Throwable
     */
    @SuppressWarnings("all")
    public Object execute() throws Throwable {
        long startMills = System.currentTimeMillis();
        final ICache cache = context.target();
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.newInstance()
                .startMills(startMills)
                .method(context.method())
                .params(context.params())
                .cache(context.target());

        CacheInterceptor cacheInterceptor = context.interceptor();
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, true);

        Object result = context.process();
        final long endMills = System.currentTimeMillis();
        interceptorContext.endMills(endMills).result(result);

        // 方法执行完成
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, false);
        return result;
    }


    /**
     * 拦截器执行类
     * @param cacheInterceptor 缓存拦截器
     * @param interceptorContext 上下文
     * @param cache 缓存
     * @param before 是否执行
     */
    @SuppressWarnings("all")
    private void interceptorHandler(CacheInterceptor cacheInterceptor,
                                    CacheInterceptorContext interceptorContext,
                                    ICache cache,
                                    boolean before){
        if(cacheInterceptor != null){
            if(cacheInterceptor.common()){
                for (ICacheInterceptor interceptor : commonInterceptors) {
                    if(before){
                        interceptor.before(interceptorContext);
                    } else {
                        interceptor.after(interceptorContext);
                    }
                }
            }

            // aof追加
            ICachePersist cachePersist = cache.persist();
            if(cacheInterceptor.aof() && (cachePersist instanceof CachePersistAOF)){
                if(before){
                    persistInterceptor.before(interceptorContext);
                } else {
                    persistInterceptor.after(interceptorContext);
                }
            }
            // evict监听器
            if(cacheInterceptor.evict()){
                if(before) {
                    evictInterceptor.before(interceptorContext);
                } else {
                    evictInterceptor.after(interceptorContext);
                }
            }
        }

    }


}
