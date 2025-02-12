package top.brightsunshine.localcache.cacheInterface;

import java.util.Map;
import java.util.Set;

public interface ICacheExpire<K, V> {

    void expireKeyAt(K key, long expireAt);

    void expireKey(K key, long expire);

//    void lazyDeleteAllExpiredKeys(Set<K> keys);

    void lazyDeleteAllExpiredKeys();

    Long expireTime(K key);

    void tryToDeleteExpiredKey(K key);

    void deleteAllKeys();

    Map<K, Long> expireTimes();
}
