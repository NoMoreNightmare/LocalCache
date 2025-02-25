package top.brightsunshine.localcache.core;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.*;
import top.brightsunshine.localcache.core.evict.LFUCacheEvict;
import top.brightsunshine.localcache.core.evict.LRUCacheEvict;
import top.brightsunshine.localcache.core.evict.WTinyLFUCacheEvict;
import top.brightsunshine.localcache.core.expire.CacheExpirePeriodic;
import top.brightsunshine.localcache.cacheInterface.ICacheRemoveListener;
import top.brightsunshine.localcache.core.expire.CacheExpireTimeWheel;
import top.brightsunshine.localcache.core.load.CacheAofLoader;
import top.brightsunshine.localcache.core.load.CacheRdbLoader;
import top.brightsunshine.localcache.core.load.NoLoader;
import top.brightsunshine.localcache.core.persist.CacheNoPersist;
import top.brightsunshine.localcache.core.persist.CachePersistAOF;
import top.brightsunshine.localcache.core.persist.CachePersistRDB;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static top.brightsunshine.localcache.core.constant.CacheEvictConstant.*;
import static top.brightsunshine.localcache.core.constant.CacheExpireConstant.*;
import static top.brightsunshine.localcache.core.constant.CacheLoadConstant.*;
import static top.brightsunshine.localcache.core.constant.CachePersistConstant.*;

public class Cache<K,V> implements ICache<K,V> {
    /**
     * 容量
     */
    private int capacity;


    /**
     * 存储缓存的hash表
     */
    private Map<K, V> map;

    /**
     * 内存淘汰策略
     */
    ICacheEvict<K, V> cacheEvict;

    /**
     * 过期策略
     */
    ICacheExpire<K, V> cacheExpire;


    /**
     * 持久化策略
     */
    ICachePersist<K, V> cachePersist;

    /**
     * 持久化对应的加载策略
     */
    ICacheLoader<K, V> cacheLoader;

    /**
     * 删除监听类
     */
//    ICacheRemoveListener<K, V> removeListener;
    List<ICacheRemoveListener<K, V>> removeListeners;

    /**
     * 慢操作监听类
     */
    List<ICacheSlowListener<K, V>> slowListeners;

    @Override
    public int getCapacity(){
        return capacity;
    }


    @Override
    public ICache<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    @Override
    public Map<K, V> map(){
        return map;
    }

    @Override
    public ICache<K, V> capacity(int newCapacity) {
        this.capacity = newCapacity;
        return this;
    }

    @Override
    @CacheInterceptor(evictAllExpired = true)
    public int size() {
        return this.map.size();
    }

    @Override
    @CacheInterceptor(evictAllExpired = true)
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    @CacheInterceptor(evict = true, slow = true)
    public boolean containsKey(Object key) {
        K keyKey = (K) key;
        cacheExpire.tryToDeleteExpiredKey(keyKey);
        return map.containsKey(key);
    }

    @Override
    @CacheInterceptor(evictAllExpired = true, slow = true)
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @CacheInterceptor(evict = true, slow = true)
    public V get(Object key) {
        K keyKey = (K) key;
        cacheExpire.tryToDeleteExpiredKey(keyKey);
        return map.get(key);
    }

    @Override
    @CacheInterceptor(evict = true, persist = true, slow = true)
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    @CacheInterceptor(evict = true, persist = true, slow = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    @CacheInterceptor(evict = true, persist = true, slow = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(evictAllExpired = true, persist = true, slow = true)
    public void clear() {
        map.clear();
    }

    @Override
    @CacheInterceptor(evictAllExpired = true, slow = true)
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @CacheInterceptor(evictAllExpired = true, slow = true)
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @CacheInterceptor(evictAllExpired = true, slow = true)
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public ICacheEvict<K, V> getEvictStrategy() {
        return cacheEvict;
    }

    @Override
    public ICache<K, V> evictStrategy(ICacheEvict<K, V> strategy) {
        this.cacheEvict = strategy;
        return this;
    }

    @Override
    public ICacheExpire<K, V> cacheExpire(){
        return cacheExpire;
    }

    /**
     * 过期策略
     */
    @Override
    @CacheInterceptor(evict = true, persist = true, slow = true)
    public void put(K key, V value, long expire) {
        this.put(key, value);
        cacheExpire.expireKey(key, expire);
    }

    @Override
    @CacheInterceptor(evict = true, persist = true, slow = true)
    public void expire(K key, long expire) {
        cacheExpire.expireKey(key, expire);
    }

    @Override
    @CacheInterceptor(evict = true, persist = true, slow = true)
    public void expireAt(K key, long expireAt) {
        cacheExpire.expireKeyAt(key, expireAt);
    }

    @Override
    public ICacheExpire<K, V> getExpireStrategy() {
        return cacheExpire;
    }

    @Override
    public ICachePersist<K, V> getPersistStrategy() {
        return cachePersist;
    }

    @Override
    public List<ICacheRemoveListener<K, V>> getRemoveListeners() {
        return removeListeners;
    }

    @Override
    public ICache<K, V> addRemoveListener(List<ICacheRemoveListener<K, V>> listener) {
        this.removeListeners = listener;
        return this;
    }

    @Override
    public List<ICacheSlowListener<K, V>> getSlowListeners() {
        return slowListeners;
    }

    @Override
    public ICache<K, V> addSlowListener(ICacheSlowListener<K, V> listener) {
        this.slowListeners.add(listener);
        return this;
    }

    public ICache<K, V> initExpire(int cacheExpire){
        switch (cacheExpire){
            case PERIODIC_EXPIRE : {
                this.cacheExpire = new CacheExpirePeriodic<>(this);
                break;
            }
            case TIME_WHEEL_EXPIRE: {
                this.cacheExpire = new CacheExpireTimeWheel<>(this);
                break;
            }
            default: {
                this.cacheExpire = new CacheExpirePeriodic<>(this);
                break;
            }
        }

        return this;
    }

    public ICache<K, V> initPersist(int cachePersist, int persistTimeInfo, String filepath){
        switch (cachePersist){
            case AOF_PERSIST: {
                this.cachePersist = new CachePersistAOF<>(this, persistTimeInfo, filepath);
                break;
            }
            case RDB_PERSIST: {
                this.cachePersist = new CachePersistRDB<>(this, persistTimeInfo, filepath);
                break;
            }
            case NONE_PERSIST: {
                this.cachePersist = new CacheNoPersist<>();
                break;
            }
            default : {
                this.cachePersist = new CacheNoPersist<>();
                break;
            }
        }
        return this;
    }

    public ICache<K, V> initLoader(int cacheLoader, String filepath){
        switch (cacheLoader){
            case AOF_LOAD:{
                this.cacheLoader = new CacheAofLoader<>(this, filepath);
                break;
            }
            case RDB_LOAD: {
                this.cacheLoader = new CacheRdbLoader<>(this, filepath);
                break;
            }
            case NO_LOADER:{
                this.cacheLoader = new NoLoader<>();
                break;
            }
            default: {
                this.cacheLoader = new NoLoader<>();
                break;
            }

        }
        return this;
    }

    public void initEvict(int cacheEvict) {
        switch (cacheEvict){
            case LRU: {
                this.cacheEvict = new LRUCacheEvict<>(this);
                break;
            }
            case LFU: {
                this.cacheEvict = new LFUCacheEvict<>(this);
                break;
            }
            case W_TINY_LFU: {
                this.cacheEvict = new WTinyLFUCacheEvict<>(this);
                break;
            }
            default: {
                this.cacheEvict = new WTinyLFUCacheEvict<>(this);
                break;
            }
        }
    }

    public ICache<K, V> noLoader(){
        this.cacheLoader = new NoLoader<>();
        return this;
    }

    public void init(){
        this.cacheLoader.load();
    }

}

