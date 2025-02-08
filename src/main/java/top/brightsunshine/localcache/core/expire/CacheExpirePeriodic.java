package top.brightsunshine.localcache.core.expire;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheExpire;
import top.brightsunshine.localcache.core.listener.ICacheRemoveListener;
import top.brightsunshine.localcache.core.listener.remove.CacheRemoveConstant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheExpirePeriodic<K, V> implements ICacheExpire<K, V> {
    /**
     * 定期删除的频率
     */
    private final int DELETION_FREQUENCY = 20;

    /**
     * 样本中过期key的数量的阈值
     */
    private final double EXPIRED_KEY_PERCENTAGE = 0.2;
    /**
     * 每次抽取的样本数量
     */
    private final int SAMPLE_SIZE = 50;

    /**
     * 定期删除的时长阈值
     */
    private final int MAXIMUM_DELETION_TIME = 10;

    /**
     * 过期字典
     */
    private Map<K, Long> expireDict = new HashMap<>();

    /**
     * 缓存
     */
    private ICache<K, V> cache;

    /**
     * 默认的删除日志
     */
    private String removeLog = "remove.log";
    /**
     * 删除监听器
     */
    private ICacheRemoveListener<K, V> removeListener;

    /**
     * 定期执行类
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpirePeriodic(ICache<K, V> cache) {
        this.cache = cache;
        this.removeListener = cache.getRemoveListener();
        //初始化一个定时任务，每隔50ms
        EXECUTOR_SERVICE.scheduleAtFixedRate(new PeriodicDeletionTask(), 1000, 1000 / DELETION_FREQUENCY, TimeUnit.MILLISECONDS);
    }

    private class PeriodicDeletionTask implements Runnable {

        @Override
        public void run() {
            long limitTime = System.currentTimeMillis() + MAXIMUM_DELETION_TIME;
            while (System.currentTimeMillis() < limitTime) {
                int count = 0;
                int currentSampleSize = 0;
                Set<K> keyToRemove = new HashSet<>();
                for (K key : expireDict.keySet()) {
                    if(key != null && expireDict.get(key) <= System.currentTimeMillis()) {
                        count++;
                        keyToRemove.add(key);
                    }
                    currentSampleSize += 1;
                    if(currentSampleSize > SAMPLE_SIZE) {
                        break;
                    }
                }

                for (K key : keyToRemove) {
                    tryToDeleteExpiredKey(key);
                }

                if(count < SAMPLE_SIZE * EXPIRED_KEY_PERCENTAGE) {
                    break;
                }
            }
        }
    }

    /**
     * 设置key的过期时间戳
     */
    @Override
    public void expireKeyAt(K key, long expireAt) {
        if(cache.containsKey(key)) {
            expireDict.put(key, expireAt);
        }
    }

    /**
     * 设置key的过期时间
     * @param key
     * @param expire
     */
    @Override
    public void expireKey(K key, long expire) {
        if(cache.containsKey(key)) {
            expireDict.put(key, System.currentTimeMillis() + expire);
        }
    }

    /**
     * 惰性删除整个map中所有过期的key
     */
    @Override
    public void lazyDeleteAllExpiredKeys() {
        Set<K> keyToRemove = new HashSet<>(expireDict.keySet());
        for (K key : keyToRemove) {
            tryToDeleteExpiredKey(key);
        }
    }

    /**
     * 返回key的过期时间
     * @param key
     * @return
     */
    @Override
    public Long expireTime(K key) {
        return expireDict.get(key);
    }

    /**
     * 如果key已过期则删除
     * @param key
     */
    @Override
    public void tryToDeleteExpiredKey(K key){
        Long expireTime = expireDict.get(key);
        if(expireTime != null){
            long currTime = System.currentTimeMillis();
            if(expireTime <= currTime){
                expireDict.remove(key);
                V value = cache.remove(key);
                cache.getEvictStrategy().deleteKey(key, cache);
                removeListener.listen(key, value, CacheRemoveConstant.REMOVE_EXPIRE);
            }
        }
    }

    @Override
    public void deleteAllKeys() {
        expireDict.clear();
    }
}
