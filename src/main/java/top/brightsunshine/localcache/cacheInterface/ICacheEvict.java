package top.brightsunshine.localcache.cacheInterface;

import top.brightsunshine.localcache.core.entry.CacheEntry;

import java.util.Map;

public interface ICacheEvict<K, V> {

    /**
     * 尝试淘汰key
     * @param cache
     * @return
     */
    public CacheEntry<K, V> evict(ICache<K, V> cache);

    /**
     * 新增或更新key
     * @param key
     */
    void updateStatus(K key, ICache<K, V> cache);

    /**
     * 删除key
     * @param key
     */
    void deleteKey(K key, ICache<K, V> cache);
}