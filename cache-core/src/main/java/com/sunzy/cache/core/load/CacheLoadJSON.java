package com.sunzy.cache.core.load;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICacheLoad;
import com.sunzy.cache.core.model.PersistEntry;

import java.util.List;

public class CacheLoadJSON<K, V> implements ICacheLoad<K, V> {

    private static final Log log = LogFactory.getLog(CacheLoadJSON.class);

    private String dbPath;

    public CacheLoadJSON(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {

        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[load] 开始处理 path: {}", dbPath);
        if(CollectionUtil.isEmpty(lines)){
            log.info("[load] path: {} 文件内容为空，直接返回", dbPath);
            return;
        }

        for (String line : lines) {
            if(StringUtil.isEmpty(line)){
                continue;
            }

            // 反序列化 将文件中的数据保存到cache中
            PersistEntry<K, V> persistEntry = JSON.parseObject(line, PersistEntry.class);
            K key = persistEntry.getKey();
            V value = persistEntry.getValue();
            Long expireTime = persistEntry.getExpireTime();

            cache.put(key, value);
            if(!ObjectUtil.isEmpty(expireTime)){
                cache.expireAt(key, expireTime);
            }
        }
    }
}
