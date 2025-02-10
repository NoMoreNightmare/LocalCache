package top.brightsunshine.localcache.core.evict.wtinylfu;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.entry.CacheEntry;
import top.brightsunshine.localcache.core.entry.LRUEntry;

import java.util.HashMap;
import java.util.Map;

public class LRU<K, V> {
    /**
     * 双向循环链表
     *
     */
    LRUEntry<K, V> head;
    /**
     * key到链表节点的映射
     */
    Map<K, LRUEntry<K, V>> index;

    /**
     * LRU容量
     */
    int capacity;

    public LRU(int capacity) {
        head = new LRUEntry<>(null, null);
        head.setNext(head);
        head.setPrev(head);
        this.capacity = capacity;
        index = new HashMap<>();
    }

    public K evict(K key) {
        if(index.size() < capacity || index.containsKey(key)){
            put(key);
            return null;
        }else{
            //开始淘汰
            LRUEntry<K, V> prev = head.getPrev();
            LRUEntry<K, V> newPrev = prev.getPrev();

            newPrev.setNext(head);
            head.setPrev(newPrev);
            index.remove(prev.key());

            put(key);
            return prev.key();
        }
    }

    public void put(K key) {
        if(index.containsKey(key)){
            deleteKey(key);
        }

        LRUEntry<K, V> entry = new LRUEntry<>(key, null);
        LRUEntry<K, V> oldNext = head.getNext();

        head.setNext(entry);
        entry.setPrev(head);

        oldNext.setPrev(entry);
        entry.setNext(oldNext);
        index.put(key, entry);
    }

    public void deleteKey(K key) {
        LRUEntry<K, V> entryToRemove = index.get(key);

        if(entryToRemove != null){
            LRUEntry<K, V> prev = entryToRemove.getPrev();
            LRUEntry<K, V> next = entryToRemove.getNext();

            prev.setNext(next);
            next.setPrev(prev);

//            entryToRemove.setNext(null);
//            entryToRemove.setPrev(null);

            index.remove(key);
        }

    }

    public boolean containsKey(K key) {
        return index.containsKey(key);
    }

    public int size() {
        return index.size();
    }
}
