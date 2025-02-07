package top.brightsunshine.localcache.core;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.core.entry.CacheEntry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Cache<K,V> implements ICache<K,V> {
    /**
     * 容量
     */
    private int capacity;

    /**
     * 当前缓存数量
     */
    private int size = 0;

    /**
     * 存储缓存的hash表
     */
    private Map<K, V> map;

    /**
     * 内存淘汰策略
     */
    ICacheEvict<K, V> cacheEvict;

    @Override
    public int getCapacity(){
        return capacity;
    }

    @Override
    public void incrementSize() {
        this.size++;
    }

    @Override
    public void decrementSize() {
        this.size--;
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
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @CacheInterceptor(evict = true)
    public V get(Object key) {
        K keyKey = (K) key;

        return map.get(key);
    }

    @Override
    @CacheInterceptor(evict = true)
    public V put(K key, V value) {
        CacheEntry<K, V> evict = cacheEvict.evict(key, this);
//        cacheEvict.updateStatus(key, this);
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
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
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


    /**
     * 过期策略
     */

    /**
     * 删除监听类
     */

    /**
     * 慢日志监听类
     */
}
