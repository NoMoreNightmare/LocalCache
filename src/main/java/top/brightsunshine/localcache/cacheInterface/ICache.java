package top.brightsunshine.localcache.cacheInterface;

import java.util.List;
import java.util.Map;

public interface ICache<K, V> extends Map<K, V> {

    /**
     * 获取cache的容量
     * @return
     */
    int getCapacity();

    /**
     * cache的缓存项数量加1
     */
    void incrementSize();

    /**
     * cache的缓存项数量减1
     */
    void decrementSize();

    /**
     * 设置cache的底层存储对象map
     */
    ICache<K, V> map(Map<K, V> map);

    /**
     * 设置cache的容量
     */
    ICache<K, V> capacity(int newCapacity);

//    /**
//     * 设置键值对的同时，设置过期时间
//     * @param key
//     * @param value
//     * @param expire
//     */
//    void put(K key, V value, long expire);
//
//    /**
//     * 设置某个key还有多久过期
//     * @param key
//     * @param expire
//     */
//    void expire(K key, long expire);
//
//    /**
//     * 设置某个key的具体过期时间
//     * @param key
//     * @param expireAt
//     */
//    void expireAt(K key, long expireAt);
//
//    /**
//     * 获取过期策略
//     * @return
//     */
//    ICacheExpire<K, V> getExpire();
//
    /**
     * 获取内存淘汰策略
     * @return
     */
    ICacheEvict<K, V> getEvictStrategy();

    ICache<K, V> evictStrategy(ICacheEvict<K, V> strategy);
//
//    /**
//     * 获取持久化策略
//     * @return
//     */
//    ICachePersist<K, V> getPersistStrategy();
//
//    /**
//     * 获取加载策略
//     * @return
//     */
//    ICacheLoader<K, V> getLoaderStrategy();
//
//    /**
//     * 获取所有慢操作的监听类
//     * @return
//     */
//    List<ICacheSlowListener> getSlowListenerStrategies();
//
//    /**
//     * 获取所有删除操作的监听类
//     * @return
//     */
//    List<ICacheDeletionListener<K, V>> getDeletionListenerStrategies();
}
