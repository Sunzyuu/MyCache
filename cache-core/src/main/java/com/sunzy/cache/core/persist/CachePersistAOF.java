package com.sunzy.cache.core.persist;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICachePersist;
import com.sunzy.cache.core.model.PersistEntry;

import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存持久化操作 AOF模式
 * @param <K>
 * @param <V>
 */
public class CachePersistAOF<K, V> extends CachePersistAdaptor<K,V>{

    private static final Log log = LogFactory.getLog(CachePersistAOF.class);


    private final List<String> bufferList = new ArrayList<>();

    /**
     * 数据库路径
     */
    private final String dbPath;

    public CachePersistAOF(String dbPath) {
        this.dbPath = dbPath;
    }


    /**
     * 持久化
     * key长度 key+value
     * 第一个空格，获取 key 的长度，然后截取
     * @param cache 缓存
     */
    @Override
    public void persist(ICache<K, V> cache) {
        // 创建文件
        if(!FileUtil.exists(dbPath)){
            FileUtil.createFile(dbPath);
        }

        FileUtil.append(dbPath, bufferList);
        bufferList.clear();
        log.info("完成AOF持久化到文件中");
    }

    /**
     * 执行延时
     * @return
     */
    @Override
    public long delay() {
        return 1;
    }

    /**
     * 执行周期
     * @return
     */
    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

    public void append(String json){
        if(StringUtil.isNotEmpty(json)){
            bufferList.add(json);
        }
    }
}
