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
    /**
     * key到链表节点的映射
     */
    private Map<K, LRUEntry<K, V>> index;

    /**
     * 缓存
     */
    private ICache<K, V> cache;

    public LRUCacheEvict(ICache<K, V> cache) {
        head = new LRUEntry<>(null, null);
        head.setNext(head);
        head.setPrev(head);
        this.cache = cache;
        index = new HashMap<>();
    }

    @Override
    public CacheEntry<K, V> evict(K key) {
        if(cache.size() < cache.getCapacity() || index.containsKey(key)){
            return null;
        }else{
            //开始淘汰
            LRUEntry<K, V> prev = head.getPrev();
            LRUEntry<K, V> newPrev = prev.getPrev();

            newPrev.setNext(head);
            head.setPrev(newPrev);
            index.remove(prev.key());

            V value = cache.remove(prev.key());
            return CacheEntry.of(prev.key(), value);
        }
    }

    @Override
    public void updateStatus(K key) {
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

    @Override
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
}
