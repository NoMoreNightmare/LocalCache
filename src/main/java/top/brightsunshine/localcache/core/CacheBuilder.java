package top.brightsunshine.localcache.core;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.core.constant.CacheExpireConstant;
import top.brightsunshine.localcache.core.constant.CacheLoadConstant;
import top.brightsunshine.localcache.core.constant.CachePersistConstant;
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

    /**
     * 默认过期策略
     */
    private int cacheExpire = CacheExpireConstant.PERIODIC_EXPIRE;

    /**
     * 默认内存持久化策略
     */
    private int cachePersist = CachePersistConstant.NONE_PERSIST;

    /**
     * 默认持久化周期
     */
    private int cachePersistTime = CachePersistConstant.AOF_ALWAYS;

    /**
     * 默认加载策略
     */
    private int cacheLoad = CacheLoadConstant.NO_LOADER;

    /**
     * 加载路径
     */
    private String persistFilepath = "";

    public CacheBuilder<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    public CacheBuilder<K, V> cacheEvict(ICacheEvict<K, V> cacheEvict) {
        this.cacheEvict = cacheEvict;
        return this;
    }

    public CacheBuilder<K, V> cacheExpire(int cacheExpire) {
        this.cacheExpire = cacheExpire;
        return this;
    }

    public CacheBuilder<K, V> noPersist() {
        this.cachePersist = CachePersistConstant.NONE_PERSIST;
        return this;
    }

    public CacheBuilder<K, V> cachePersist(int cachePersist, int cachePersistTime) {
        this.cachePersist = cachePersist;
        this.cachePersistTime = cachePersistTime;
        return this;
    }

    public CacheBuilder<K, V> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public CacheBuilder<K, V> cacheLoader(int cacheLoad, String filepath) {
        this.cacheLoad = cacheLoad;
        this.persistFilepath = filepath;
        return this;
    }

    public ICache<K, V> build(){
        Cache<K, V> cache = new Cache<>();
        cache.map(map);
        cache.capacity(capacity);
        cache.evictStrategy(cacheEvict);

        cache.initExpire(cacheExpire);
        cache.initPersist(cachePersist, cachePersistTime);
        cache.initLoader(cacheLoad, persistFilepath);

        cache.init();

        return CacheProxy.getProxy(cache);
    }




}
