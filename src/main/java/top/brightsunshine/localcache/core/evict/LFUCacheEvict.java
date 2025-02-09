package top.brightsunshine.localcache.core.evict;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.core.entry.CacheEntry;
import top.brightsunshine.localcache.core.entry.LFUEntry;
import top.brightsunshine.localcache.core.entry.LRUEntry;

import java.util.HashMap;
import java.util.Map;

public class LFUCacheEvict<K, V> implements ICacheEvict<K, V> {

    //key和对应entry的映射
    private Map<K, LFUEntry<K, V>> index = new HashMap<>();

    //freq和entry所代表的列表的映射
    private Map<Integer, LFUEntry<K, V>> freqToEntryList = new HashMap<>();

    //当前最小的freq
    private int minFreq = 1;

    public LFUCacheEvict() {
        LFUEntry<K, V> newHead = new LFUEntry<>(null, null, 1);
        newHead.setNext(newHead);
        newHead.setPrev(newHead);
        freqToEntryList.put(1, newHead);
    }

    @Override
    public CacheEntry<K, V> evict(K key, ICache<K, V> cache) {
        if(cache.size() < cache.getCapacity() || index.containsKey(key)) {
            return null;
        }else{
            //淘汰

            //1.获取最小频率对应的entry列表
            LFUEntry<K, V> freqLRU = freqToEntryList.get(minFreq);

            //2.内部使用LRU来进行淘汰
            LFUEntry<K, V> prev = freqLRU.getPrev();
            LFUEntry<K, V> newPrev = prev.getPrev();
//            prev.setPrev(null);
//            prev.setNext(null);

            newPrev.setNext(freqLRU);
            freqLRU.setPrev(newPrev);
            index.remove(prev.getKey());

            minFreq = 1;

            V value = cache.remove(prev.getKey());
            updateStatus(key, cache);
            return CacheEntry.of(prev.getKey(), value);
        }
    }

    @Override
    public void updateStatus(K key, ICache<K, V> cache) {

        int freq = 0;
        if(index.containsKey(key)) {
            freq = index.get(key).getFreq();
            deleteKey(key, cache);
        }else{
            minFreq = 1;
        }

        freq++;
        LFUEntry<K, V> entry = new LFUEntry<>(key, null, freq);



        if(!freqToEntryList.containsKey(freq)) {
            LFUEntry<K, V> newHead = new LFUEntry<>(null, null, freq);
            newHead.setNext(newHead);
            newHead.setPrev(newHead);
            freqToEntryList.put(freq, newHead);
        }

        LFUEntry<K, V> head = freqToEntryList.get(freq);
        LFUEntry<K, V> oldNext = head.getNext();

        head.setNext(entry);
        entry.setPrev(head);
        oldNext.setPrev(entry);
        entry.setNext(oldNext);

        index.put(key, entry);

    }

    @Override
    public void deleteKey(K key, ICache<K, V> cache) {
        LFUEntry<K, V> entryToRemove = index.get(key);
        if(entryToRemove != null) {
            LFUEntry<K, V> prev = entryToRemove.getPrev();
            LFUEntry<K, V> next = entryToRemove.getNext();

            prev.setNext(next);
            next.setPrev(prev);

            index.remove(key);

            if(isListEmpty(entryToRemove.getFreq()) && minFreq == entryToRemove.getFreq()) {
                minFreq++;
            }
        }
    }

    private boolean isListEmpty(int freq){
        if(freqToEntryList.containsKey(freq)){
            LFUEntry<K, V> head = freqToEntryList.get(freq);
            return head.getNext() == head && head.getPrev() == head;
        }

        return false;
    }
}
