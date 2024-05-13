package com.sunzy.cache.core.load;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.lang.reflect.ReflectMethodUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.annotation.CacheInterceptor;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheLoad;
import com.sunzy.cache.core.core.Cache;
import com.sunzy.cache.core.model.PersistAofEntry;
import net.sf.cglib.core.CollectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 加载策略-AOF文件按模式
 * @param <K>
 * @param <V>
 */
public class CacheLoadAof<K,V> implements ICacheLoad<K,V> {
    private static final Log log = LogFactory.getLog(CacheLoadAof.class);

    private static final Map<String, Method> METHOD_MAP = new HashMap<>();
    static {
        Method[] methods = Cache.class.getMethods();
        for (Method method : methods) {
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);
            if(cacheInterceptor != null){
                // todo
                if(cacheInterceptor.aof()){
                    String methodName = method.getName();
                    METHOD_MAP.put(methodName, method);
                }
            }
        }
    }


    private final String dbPath;

    public CacheLoadAof(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[load] 开始处理 path: {}", dbPath);
        if(CollectionUtil.isEmpty(lines)){
            log.info("[load] path: {} 文件为空，直接返回", dbPath);
            return;
        }

        for (String line : lines) {
            if(StringUtil.isEmpty(line)){
                continue;
            }
            // 反序列化，但是复杂的json失败
            PersistAofEntry entry = JSON.parseObject(line, PersistAofEntry.class);
            final String methodName = entry.getMethodName();
            final Object[] params = entry.getParams();
            final Method method = METHOD_MAP.get(methodName);
            // 反射调用
            ReflectMethodUtil.invoke(cache, method, params);
        }

    }
}
