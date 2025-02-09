package top.brightsunshine.localcache.core.entry;

public class TimeWheelNode<K, V> {
    private K key;
    private long expireAt;
    private TimeWheelNode<K, V> next;
    private TimeWheelNode<K, V> prev;

    public TimeWheelNode(K key, long expireAt) {
        this.key = key;
        this.expireAt = expireAt;
        this.next = null;
        this.prev = null;
    }

    public TimeWheelNode(K key, long expireAt, TimeWheelNode<K, V> next) {
        this.key = key;
        this.expireAt = expireAt;
        this.next = next;
        this.prev = null;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void expireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public K getKey() {
        return key;
    }

    public void key(K key) {
        this.key = key;
    }

    public TimeWheelNode<K, V> getNext() {
        return next;
    }

    public void next(TimeWheelNode<K, V> next) {
        this.next = next;
    }

    public TimeWheelNode<K, V> getPrev() {
        return prev;
    }

    public void prev(TimeWheelNode<K, V> prev) {
        this.prev = prev;
    }

}
