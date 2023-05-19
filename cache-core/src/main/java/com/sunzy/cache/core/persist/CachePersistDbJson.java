package com.sunzy.cache.core.persist;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.sunzy.cache.api.ICache;
import com.sunzy.cache.api.ICachePersist;
import com.sunzy.cache.core.model.PersistEntry;

import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CachePersistDbJson<K, V> extends CachePersistAdaptor<K, V> {

    /**
     * 数据库路径
     */
    private final String dbPath;

    public CachePersistDbJson(String dbPath) {
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
        Set<Map.Entry<K, V>> entrySet = cache.entrySet();
        // 创建文件
        FileUtil.createFile(dbPath);
        // 清空文件
        FileUtil.truncate(dbPath);

        for (Map.Entry<K, V> entry : entrySet) {
            K key = entry.getKey();
            Long expireTime = cache.expire().expireTime(key);
            PersistEntry<K, V> persistEntry = new PersistEntry<>();
            persistEntry.setKey(key);
            persistEntry.setValue(entry.getValue());
            persistEntry.setExpireTime(expireTime);
            String line = JSON.toJSONString(persistEntry);
            FileUtil.write(dbPath, line, StandardOpenOption.APPEND);

        }

    }
}
