package top.brightsunshine.localcache.core.entry;


public class LFUEntry<K, V> {

    private K key;
    private V value;
    private int freq;

    public LFUEntry<K, V> getNext() {
        return next;
    }

    public LFUEntry<K, V> setNext(LFUEntry<K, V> next) {
        this.next = next;
        return this;
    }

    public LFUEntry<K, V> getPrev() {
        return prev;
    }

    public LFUEntry<K, V> setPrev(LFUEntry<K, V> prev) {
        this.prev = prev;
        return this;
    }

    private LFUEntry<K, V> next;
    private LFUEntry<K, V> prev;

    public LFUEntry(K key, V value, int freq) {
        this.key = key;
        this.value = value;
        this.freq = freq;
    }

    public LFUEntry(K key, V value) {
        this.key = key;
        this.value = value;
        this.freq = 1;
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

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int incrementFreq() {
        this.freq++;
        return this.freq;
    }


}
