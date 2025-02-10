package top.brightsunshine.localcache.core.evict.wtinylfu;

import top.brightsunshine.localcache.core.entry.CacheEntry;
import top.brightsunshine.localcache.core.entry.LRUEntry;

import java.util.Random;

public class ProbationLRU<K, V> extends LRU<K, V>{
    private TinyLFU<K, V> tinyLfu;

    public ProbationLRU(int capacity, TinyLFU<K, V> tinyLfu) {
        super(capacity);
        this.tinyLfu = tinyLfu;
    }

    public K evict(K candidate, boolean fromWindow) {

        K keyToRemove = null;

        if(index.size() < capacity) {
            LRUEntry<K, V> newEntry = new LRUEntry<>(candidate, null);
            LRUEntry<K, V> preNext = head.getNext();

            head.setNext(newEntry);
            newEntry.setPrev(head);
            preNext.setPrev(newEntry);
            newEntry.setNext(preNext);
            index.put(candidate, newEntry);
            return keyToRemove;
        }

        if(fromWindow){
            //从window cache区，直接比较访问频率
            LRUEntry<K, V> victim = head.getPrev();
            int candidateCount = tinyLfu.getCount(candidate);
            int victimCount = tinyLfu.getCount(victim.key());
            if(candidateCount > victimCount){
                keyToRemove = victim.key();
                LRUEntry<K, V> newPrev = victim.getPrev();
                newPrev.setNext(head);
                head.setPrev(newPrev);
                index.remove(victim.key());
                put(candidate);
            }else{
                keyToRemove = candidate;
            }
        }else{
            //从protected区域，比较访问区域
            LRUEntry<K, V> victim = head.getPrev();
            int candidateCount = tinyLfu.getCount(candidate);
            int victimCount = tinyLfu.getCount(victim.key());
            if(candidateCount > victimCount){
                keyToRemove = victim.key();
                LRUEntry<K, V> newPrev = victim.getPrev();
                newPrev.setNext(head);
                head.setPrev(newPrev);
                index.remove(victim.key());
                put(candidate);
            }else if(candidateCount < victimCount && candidateCount <= 5){
                keyToRemove = candidate;
            }else{
                Random random = new Random();
                boolean evictVictim = random.nextBoolean();
                if(evictVictim){
                    keyToRemove = victim.key();
                    LRUEntry<K, V> newPrev = victim.getPrev();
                    newPrev.setNext(head);
                    head.setPrev(newPrev);
                    index.remove(victim.key());
                    put(candidate);
                }else{
                    keyToRemove = candidate;
                }
            }
        }

        return keyToRemove;
    }



}
