package top.brightsunshine.localcache.core;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.cacheInterface.ICacheExpire;
import top.brightsunshine.localcache.core.entry.CacheEntry;
import top.brightsunshine.localcache.core.expire.CacheExpirePeriodic;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static top.brightsunshine.localcache.core.constant.CacheExpireContant.PERIODIC_EXPIRE;

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
    @CacheInterceptor(evict = true)
    public boolean containsKey(Object key) {
        K keyKey = (K) key;
        cacheExpire.tryToDeleteExpiredKey(keyKey);
        return map.containsKey(key);
    }

    @Override
    @CacheInterceptor(evictAllExpired = true)
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @CacheInterceptor(evict = true)
    public V get(Object key) {
        K keyKey = (K) key;
        cacheExpire.tryToDeleteExpiredKey(keyKey);
        return map.get(key);
    }

    @Override
    @CacheInterceptor(evict = true)
    public V put(K key, V value) {
        CacheEntry<K, V> evict = cacheEvict.evict(key, this);
        return map.put(key, value);
    }

    @Override
    @CacheInterceptor(evict = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(evictAllExpired = true)
    public void clear() {
        map.clear();
    }

    @Override
    @CacheInterceptor(evictAllExpired = true)
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @CacheInterceptor(evictAllExpired = true)
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @CacheInterceptor(evictAllExpired = true)
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
    @CacheInterceptor(evict = true)
    public void put(K key, V value, long expire) {
        this.put(key, value);
        cacheExpire.expireKey(key, expire);
    }

    @Override
    @CacheInterceptor(evict = true)
    public void expire(K key, long expire) {
        if(this.containsKey(key)) {
            cacheExpire.expireKey(key, expire);
        }
    }

    @Override
    @CacheInterceptor(evict = true)
    public void expireAt(K key, long expireAt) {
        if(this.containsKey(key)) {
            cacheExpire.expireKeyAt(key, expireAt);
        }
    }

    @Override
    public ICacheExpire<K, V> getExpireStrategy() {
        return cacheExpire;
    }




    /**
     * 删除监听类
     */

    /**
     * 慢日志监听类
     */


    public void init(int cacheExpire){
        switch (cacheExpire){
            case PERIODIC_EXPIRE : {
                this.cacheExpire = new CacheExpirePeriodic<>(this);
                break;
            }
            default : {
                this.cacheExpire = new CacheExpirePeriodic<>(this);
                break;
            }

        }
    }
}
