import org.junit.jupiter.api.Test;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.Cache;
import top.brightsunshine.localcache.core.CacheBuilder;
import top.brightsunshine.localcache.core.evict.LRUCacheEvict;

import java.util.HashMap;
import java.util.Map;

public class CacheTest {
    @Test
    public void testCache(){
        ICache<String, String> cache = new Cache<String, String>();
        cache.map(new HashMap<>())
                .capacity(3)
                .evictStrategy(new LRUCacheEvict<>());

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        System.out.println(cache.size());
        System.out.println(cache.get("key1"));
        cache.put("key4", "value4");
        System.out.println(cache.get("key1"));

        System.out.println(cache.get("key4"));
    }

    @Test
    public void testCacheBuilderAOP(){
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> build = cacheBuilder.capacity(3).map(new HashMap<>()).build();
        build.put("key1", "value1");
        build.put("key2", "value2");
        build.put("key3", "value3");
        build.get("key1");
        build.put("key4", "value4");
        System.out.println(build.get("key1"));
    }
}
