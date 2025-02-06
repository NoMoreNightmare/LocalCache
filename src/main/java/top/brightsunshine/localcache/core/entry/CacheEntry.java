package top.brightsunshine.localcache.core.entry;

public class CacheEntry<K, V> {
    private K key;
    private V value;

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> CacheEntry<K, V> of(K key, V value){
        return new CacheEntry<>(key, value);
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
