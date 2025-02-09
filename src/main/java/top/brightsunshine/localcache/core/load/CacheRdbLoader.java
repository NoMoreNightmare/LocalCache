package top.brightsunshine.localcache.core.load;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheLoader;
import top.brightsunshine.localcache.core.entry.RdbPersistEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class CacheRdbLoader<K, V> implements ICacheLoader<K, V> {

    private ICache<K, V> cache;

    private String rdbPath;

    public CacheRdbLoader(ICache<K, V> cache, String rdbPath) {
        this.cache = cache;
        this.rdbPath = rdbPath;
    }

    @Override
    public void load() {
        File file = new File(rdbPath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ObjectMapper mapper = new ObjectMapper();
            reader.lines().forEach(line -> {
                try {
                    RdbPersistEntry<K, V> rdbPersistEntry = mapper.readValue(line, new TypeReference<RdbPersistEntry<K, V>>() {
                    });
                    if(rdbPersistEntry.getExpireAt() != null) {
                        cache.put(rdbPersistEntry.getKey(), rdbPersistEntry.getValue());
                        cache.expireAt(rdbPersistEntry.getKey(), rdbPersistEntry.getExpireAt());
                    }else{
                        cache.put(rdbPersistEntry.getKey(), rdbPersistEntry.getValue());
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
