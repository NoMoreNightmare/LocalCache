package top.brightsunshine.localcache.core.listener;

public interface ICacheRemoveListener<K, V> {
    void listen(K key, V value, String type);
}
