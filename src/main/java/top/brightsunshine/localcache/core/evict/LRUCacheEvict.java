package top.brightsunshine.localcache.core.evict;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.core.entry.CacheEntry;
import top.brightsunshine.localcache.core.entry.LRUEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU淘汰策略
 * @param <K>
 * @param <V>
 */
public class LRUCacheEvict<K, V> implements ICacheEvict<K, V> {

    /**
     * 双向循环链表
     *
     */
    private LRUEntry<K, V> head;
    Map<K, LRUEntry<K, V>> index;

    public LRUCacheEvict() {
        head = new LRUEntry<>(null, null);
        head.setNext(head);
        head.setPrev(head);

        index = new HashMap<>();
    }

    @Override
    public CacheEntry<K, V> evict(K key, ICache<K, V> cache) {
        if(cache.size() < cache.getCapacity()){
            updateStatus(key, cache);
            return null;
        }else{
            //开始淘汰
            LRUEntry<K, V> prev = head.getPrev();
            LRUEntry<K, V> newPrev = prev.getPrev();
//            prev.setPrev(null);
//            prev.setNext(null);

            newPrev.setNext(head);
            head.setPrev(newPrev);
            index.remove(prev.key());
            cache.remove(prev.key());

            cache.decrementSize();
            return CacheEntry.of(prev.key(), prev.value());
        }
    }

    @Override
    public void updateStatus(K key, ICache<K, V> map) {
        if(map.containsKey(key)){
            deleteKey(key, map);
            map.decrementSize();
        }

        LRUEntry<K, V> entry = new LRUEntry<>(key, null);
        LRUEntry<K, V> oldNext = head.getNext();

        head.setNext(entry);
        entry.setPrev(head);

        oldNext.setPrev(entry);
        entry.setNext(oldNext);
        map.incrementSize();
        index.put(key, entry);
    }

    @Override
    public void deleteKey(K key, ICache<K, V> map) {
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
}
