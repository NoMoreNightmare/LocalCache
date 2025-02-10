package top.brightsunshine.localcache.cacheInterface;

import top.brightsunshine.localcache.core.entry.CacheEntry;

import java.util.Map;

public interface ICacheEvict<K, V> {

    /**
     * 尝试淘汰key
     * @return
     */
    public CacheEntry<K, V> evict(K key);

    /**
     * 新增或更新key
     * @param key
     */
    CacheEntry<K, V> updateStatus(K key);

    /**
     * 删除key
     * @param key
     */
    void deleteKey(K key);
}