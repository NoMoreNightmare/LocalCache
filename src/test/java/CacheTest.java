import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.Cache;
import top.brightsunshine.localcache.core.CacheBuilder;
import top.brightsunshine.localcache.core.constant.CacheExpireContant;
import top.brightsunshine.localcache.core.evict.LRUCacheEvict;
import org.junit.*;
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
        Assert.assertEquals(cache.size(), 3);
        Assert.assertEquals(cache.get("key1"), "value1");
        cache.put("key4", "value4");
        Assert.assertEquals(null, cache.get("key1"));
        Assert.assertEquals("value4", cache.get("key4"));
    }

    @Test
    public void testCacheBuilderAOP(){
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> build = cacheBuilder.capacity(3).map(new HashMap<>()).build();
        build.put("key1", "value1", 1000);
        build.put("key2", "value2");
        build.put("key3", "value3");
        Assert.assertTrue(build.get("key1") != null);
        build.put("key4", "value4");
        Assert.assertEquals(build.get("key1"), null);
    }

    @Test
    public void testCacheExpirePeriod() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> build = cacheBuilder.capacity(3).map(new HashMap<>()).build(CacheExpireContant.PERIODIC_EXPIRE);
        build.put("key1", "value1", 3000);
        build.put("key2", "value1", 2000);
        build.put("key3", "value1", 3000);
        Thread.sleep(10000);
        Assert.assertEquals(null, build.get("key3"));
    }

    @Test
    public void testEvictAllExpireInterceptor() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> build = cacheBuilder.capacity(100).map(new HashMap<>()).build(CacheExpireContant.PERIODIC_EXPIRE);


        for (int i = 0; i < 50; i++) {
            build.put("key" + i, "value" + i, 10);
        }

        for(int i = 50; i < 100; i++) {
            build.put("key" + i, "value" + i, 2000);
        }

        Thread.sleep(10);
        Assert.assertEquals(50, build.size());
    }
}
