package top.brightsunshine.localcache.core;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.core.evict.LRUCacheEvict;
import top.brightsunshine.localcache.core.proxy.CacheProxy;

import java.util.HashMap;
import java.util.Map;

public class CacheBuilder<K, V> {

    /**
     * 底层map
     */
    private Map<K, V> map = new HashMap<K, V>();

    /**
     * 默认容量
     */
    private int capacity = 2000;

    /**
     * 默认内存淘汰策略
     */
    private ICacheEvict<K, V> cacheEvict = new LRUCacheEvict<>();

    public CacheBuilder<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    public CacheBuilder<K, V> cacheEvict(ICacheEvict<K, V> cacheEvict) {
        this.cacheEvict = cacheEvict;
        return this;
    }

    public CacheBuilder<K, V> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public ICache<K, V> build(){
        ICache<K, V> cache = new Cache<>();
        cache.map(map);
        cache.capacity(capacity);
        cache.evictStrategy(cacheEvict);

        return CacheProxy.getProxy(cache);
    }
}
