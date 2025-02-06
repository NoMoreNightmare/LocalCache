package top.brightsunshine.localcache.core.entry;

public class LRUEntry<K, V> {
    private K key;
    private V value;
    private LRUEntry<K, V> next;
    private LRUEntry<K, V> prev;

    public LRUEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K key(){
        return key;
    }

    public V value(){
        return value;
    }

    public LRUEntry<K, V> setNext(LRUEntry<K, V> next) {
        this.next = next;
        next.prev = this;
        return this;
    }

    public LRUEntry<K, V> setPrev(LRUEntry<K, V> prev) {
        this.prev = prev;
        prev.next = this;
        return this;
    }

    public LRUEntry<K, V> getPrev() {
        return prev;
    }

    public LRUEntry<K, V> getNext() {
        return next;
    }
}
