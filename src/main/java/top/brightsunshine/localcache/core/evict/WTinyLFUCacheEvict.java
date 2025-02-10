package top.brightsunshine.localcache.core.evict;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.core.entry.CacheEntry;
import top.brightsunshine.localcache.core.evict.wtinylfu.LRU;
import top.brightsunshine.localcache.core.evict.wtinylfu.ProbationLRU;
import top.brightsunshine.localcache.core.evict.wtinylfu.TinyLFU;

public class WTinyLFUCacheEvict<K, V> implements ICacheEvict<K, V> {
    /**
     * 缓存
     */
    private ICache<K, V> cache;
    /**
     * window cache
     */
    private LRU<K, V> windowCache;

    /**
     * window cache的大小
     */
    private double WINDOW_SIZE_PERCENT = 0.01;

    /**
     * main cache的大小
     */
    private double MAIN_SIZE_PERCENT = 0.99;

    /**
     * probation区
     */
    private LRU<K, V> probationCache;

    /**
     * probation cache的大小
     */
    private double PROBATION_SIZE_PERCENT = 0.2;

    /**
     * protected区
     */
    private LRU<K, V> protectedCache;

    /**
     * protected cache的大小
     */
    private double PROTECTED_SIZE_PERCENT = 0.8;

    /**
     * 维护频率统计
     */
    private TinyLFU<K, V> tinyLFU;

    public WTinyLFUCacheEvict(ICache<K, V> cache) {
        this.cache = cache;
        this.windowCache = new LRU<>((int) Math.ceil(cache.getCapacity() * WINDOW_SIZE_PERCENT));
        this.protectedCache = new LRU<>((int) Math.ceil(cache.getCapacity() * PROTECTED_SIZE_PERCENT * MAIN_SIZE_PERCENT));
        this.tinyLFU = new TinyLFU<>(cache.getCapacity());
        this.probationCache = new ProbationLRU<>((int) Math.ceil(cache.getCapacity() * PROBATION_SIZE_PERCENT * MAIN_SIZE_PERCENT), tinyLFU);
    }

    @Override
    public CacheEntry<K, V> evict(K key) {
        //1.判断数据结构里有没有这个key
        if(protectedCache.containsKey(key) || windowCache.containsKey(key) || probationCache.containsKey(key)) {
            return null;
        }

        //2.没有key则放入window cache
        K evictedKey = windowCache.evict(key);
        if (evictedKey != null) {
            //3.放入probation区
            K victim = ((ProbationLRU<K, V>)probationCache).evict(evictedKey, true);
            if (victim != null) {
                V value = cache.remove(victim);
                return new CacheEntry<>(victim, value);
            }
        }

        return null;
    }

    /**
     * 主要更新访问频率和把key从probation区迁移到protected区
     * @param key
     * @return
     */
    @Override
    public CacheEntry<K, V> updateStatus(K key) {
        //注：因为在evict阶段，新的key的情况已经被处理（已经淘汰不符合要求的key，并加入新的key）
        //因此不需要再考虑新的key进入WTinyLFU的情况，只需要考虑本就存在的key的情况，以及新key和旧key的访问频率增加
        if(windowCache.containsKey(key) || probationCache.containsKey(key) || protectedCache.containsKey(key)) {
            //增加访问频率
            tinyLFU.increment(key);
            if(probationCache.containsKey(key)) {
                probationCache.put(key);
                //这个key达到了提升到protected区的阈值
                if(tinyLFU.needToUpgrade(key)){
                    probationCache.deleteKey(key);
                    //protected区淘汰掉一个key
                    K evictedKey = protectedCache.evict(key);
                    if(evictedKey != null) {
                        //进入probation区，比较决定淘汰哪一个key
                        K victim = ((ProbationLRU<K, V>)probationCache).evict(evictedKey, false);
                        if(victim != null){
                            V value = cache.remove(victim);
                            CacheEntry<K, V> entry = new CacheEntry<>(evictedKey, value);
                            if(entry != null) {
                                return entry;
                            }
                        }
                    }
                }
            }
        }

//        if(protectedCache.containsKey(key)) {
//            protectedCache.put(key);
//        }else if(probationCache.containsKey(key)) {
//            probationCache.put(key);
//            if(probationCache.containsKey(key)) {
//                probationCache.put(key);
//                //增加访问频率
//                tinyLFU.increment(key);
//                //这个key达到了提升到protected区的阈值
//                if(tinyLFU.needToUpgrade(key)){
//                    probationCache.deleteKey(key);
//                    //protected区淘汰掉一个key
//                    K evictedKey = protectedCache.evict(key);
//                    if(evictedKey != null) {
//                        //进入probation区，比较决定淘汰哪一个key
//                        K victim = ((ProbationLRU<K, V>)probationCache).evict(evictedKey, false);
//                        if(victim != null){
//                            V value = cache.remove(victim);
//                            CacheEntry<K, V> entry = new CacheEntry<>(evictedKey, value);
//                            if(entry != null) {
//                                return entry;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        else if(windowCache.containsKey(key)) {
//            windowCache.put(key);
//        }

        return null;
    }

    @Override
    public void deleteKey(K key) {
        if(protectedCache.containsKey(key)){
            protectedCache.deleteKey(key);
        }else if(probationCache.containsKey(key)) {
            probationCache.deleteKey(key);
        }else if(windowCache.containsKey(key)){
            windowCache.deleteKey(key);
        }
    }
}
