package top.brightsunshine.localcache.core;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheSlowListener;
import top.brightsunshine.localcache.core.constant.CacheEvictConstant;
import top.brightsunshine.localcache.core.constant.CacheExpireConstant;
import top.brightsunshine.localcache.core.constant.CacheLoadConstant;
import top.brightsunshine.localcache.core.constant.CachePersistConstant;
import top.brightsunshine.localcache.cacheInterface.ICacheRemoveListener;
import top.brightsunshine.localcache.core.listener.remove.CacheRemoveListener;
import top.brightsunshine.localcache.core.listener.slow.CacheSlowListener;
import top.brightsunshine.localcache.core.proxy.CacheProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
//    private ICacheEvict<K, V> cacheEvict = new LRUCacheEvict<>();
    private int cacheEvict = CacheEvictConstant.LRU;


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
     * aof默认存储路径
     */
    private String defaultAofFilepath = "1.aof";

    /**
     * rdb默认存储路径
     */
    private String defaultRdbFilepath = "1.rdb";

    /**
     * aof或rdb默认存储路径
     */
    private String persistFilepath = "";

    /**
     * 默认加载策略
     */
    private int cacheLoad = CacheLoadConstant.NO_LOADER;

    /**
     * 加载路径
     */
    private String loadFilepath = "";

    /**
     * 默认的删除监听器
     */
    private List<ICacheRemoveListener<K, V>> removeListeners = new ArrayList<ICacheRemoveListener<K, V>>();
//    private ICacheRemoveListener<K, V> removeListener = new CacheRemoveListener<>();

    /**
     * 默认的慢操作监听器
     */
    private ICacheSlowListener<K, V> slowListener = new CacheSlowListener<>();

    public CacheBuilder<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    public CacheBuilder<K, V> cacheEvict(int cacheEvict) {
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

    public CacheBuilder<K, V> cachePersist(int cachePersist, int cachePersistTime, String persistFilepath) {
        this.cachePersist = cachePersist;
        this.cachePersistTime = cachePersistTime;
        this.persistFilepath = persistFilepath;
        return this;
    }

    public CacheBuilder<K, V> cachePersist(int cachePersist, int cachePersistTime) {
        this.cachePersist = cachePersist;
        this.cachePersistTime = cachePersistTime;
        if(cachePersist == CachePersistConstant.AOF_PERSIST){
            this.persistFilepath = this.defaultAofFilepath;
        }else if(cachePersist == CachePersistConstant.RDB_PERSIST){
            this.persistFilepath = this.defaultRdbFilepath;
        }
        return this;
    }

    public CacheBuilder<K, V> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public CacheBuilder<K, V> cacheLoader(int cacheLoad, String filepath) {
        this.cacheLoad = cacheLoad;
        this.loadFilepath = filepath;
        return this;
    }

    public CacheBuilder<K, V> addRemoveListener(ICacheRemoveListener<K, V> removeListener) {
        this.removeListeners.add(removeListener);
        return this;
    }

    public CacheBuilder<K, V> slowListener(ICacheSlowListener<K, V> slowListener) {
        this.slowListener = slowListener;
        return this;
    }

    public ICache<K, V> build(){
        Cache<K, V> cache = new Cache<>();
        cache.map(map);
        cache.capacity(capacity);
//        cache.evictStrategy(cacheEvict);
        this.removeListeners.add(new CacheRemoveListener<>());
        cache.addRemoveListener(removeListeners);
        cache.addSlowListener(slowListener);

        cache.initEvict(cacheEvict);
        cache.initExpire(cacheExpire);
        cache.initPersist(cachePersist, cachePersistTime, persistFilepath);
        cache.initLoader(cacheLoad, loadFilepath);

        cache.init();

        return CacheProxy.getProxy(cache);
    }




}
