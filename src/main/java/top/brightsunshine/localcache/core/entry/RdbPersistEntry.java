package top.brightsunshine.localcache.core.entry;

public class RdbPersistEntry<K, V> {
    /**
     * key
     */
    private K key;

    /**
     * value
     */
    private V value;

    /**
     * 过期时间
     */
    private long expireAt;

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}
