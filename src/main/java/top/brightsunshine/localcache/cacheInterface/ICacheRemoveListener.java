package top.brightsunshine.localcache.cacheInterface;

public interface ICacheRemoveListener<K, V> {
    void listen(K key, V value, String type);
}
