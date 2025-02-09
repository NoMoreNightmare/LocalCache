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
    private Long expireAt;

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

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }
}
