import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.Cache;
import top.brightsunshine.localcache.core.CacheBuilder;
import top.brightsunshine.localcache.core.constant.CacheEvictConstant;
import top.brightsunshine.localcache.core.constant.CacheExpireConstant;
import top.brightsunshine.localcache.core.constant.CacheLoadConstant;
import top.brightsunshine.localcache.core.constant.CachePersistConstant;
import top.brightsunshine.localcache.core.evict.LFUCacheEvict;
import top.brightsunshine.localcache.core.evict.LRUCacheEvict;
import org.junit.*;
import top.brightsunshine.localcache.core.evict.WTinyLFUCacheEvict;
import top.brightsunshine.localcache.core.listener.slow.CacheSlowListener;

import java.util.HashMap;
import java.util.Map;

import static top.brightsunshine.localcache.core.constant.CacheEvictConstant.LFU;
import static top.brightsunshine.localcache.core.constant.CacheEvictConstant.LRU;
import static top.brightsunshine.localcache.core.constant.CacheLoadConstant.RDB_LOAD;

public class CacheTest {
    @Test
    public void testCache(){
        ICache<String, String> cache = new Cache<String, String>();
        cache.map(new HashMap<>())
                .capacity(3)
                .evictStrategy(new LRUCacheEvict<>(cache));

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
        ICache<String, String> build = cacheBuilder.capacity(3).cacheEvict(LRU).map(new HashMap<>()).build();
        build.put("key1", "value1");
        build.put("key2", "value2");
        build.put("key3", "value3");
        Assert.assertTrue(build.get("key1") != null);
        build.put("key4", "value4");

        Assert.assertEquals(null, build.get("key2"));

        build.put("key5", "value5");
        build.put("key6", "value6");
        Assert.assertEquals("value4", build.get("key4"));
    }

    @Test
    public void testCacheExpirePeriod() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> build = cacheBuilder.capacity(3).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .build();
        build.put("key1", "value1", 3000);
        build.put("key2", "value1", 2000);
        build.put("key3", "value1", 3000);
        Thread.sleep(10000);
        Assert.assertEquals(null, build.get("key3"));
    }

    @Test
    public void testEvictAllExpireInterceptor() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> build = cacheBuilder.capacity(100).map(new HashMap<>()).cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE).build();


        for (int i = 0; i < 50; i++) {
            build.put("key" + i, "value" + i, 10);
        }

        for(int i = 50; i < 100; i++) {
            build.put("key" + i, "value" + i, 2000);
        }

        Thread.sleep(10);
        Assert.assertEquals(50, build.size());
    }

    @Test
    public void testAOFPersistAlways() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(100).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .build();

        for (int i = 0; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }
        Thread.sleep(1000);
    }

    @Test
    public void testAOFPersistEverySec() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(100).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_EVERYSEC, "1.aof")
                .build();

        for (int i = 0; i < 50; i++) {
            cache.put("key" + i, "value" + i);
        }
        Thread.sleep(100000);
        for (int i = 50; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }


    }

    @Test
    public void testAOFLoad() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(100).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .build();

        for (int i = 0; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }

        CacheBuilder<String, String> newCacheBuilder = new CacheBuilder<>();
        ICache<String, String> newCache = newCacheBuilder.capacity(100).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .cacheLoader(CacheLoadConstant.AOF_LOAD, "1.aof")
                .build();

        Assert.assertEquals(newCache.get("key1"), "value1");
    }

    @Test
    public void testRemoveListener() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(20).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .build();

        for (int i = 0; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }
    }

    @Test
    public void testRemoveListenerForExpired() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(20).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .build();

        for (int i = 0; i < 20; i++) {
            cache.put("key" + i, "value" + i, 20);
        }

        Thread.sleep(2000);

        for(int i = 20; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }
    }

    @Test
    public void testSlowListener() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(20).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .build();

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put("key" + i, "value" + i);
        }

        cache.putAll(map);
    }

    @Test
    public void testEvictBeforeExpire() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(20).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .build();

        for (int i = 0; i < 20; i++) {
            cache.put("key" + i, "value" + i, 20);
        }


        for(int i = 20; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }

        Thread.sleep(2000);
    }

    @Test
    public void testLFUEvict() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(3).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.PERIODIC_EXPIRE)
                .cacheEvict(LFU)
                .cachePersist(CachePersistConstant.AOF_PERSIST, CachePersistConstant.AOF_ALWAYS, "1.aof")
                .build();

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        for (int i = 0; i < 4; i++) {
            cache.get("key2");
            cache.get("key3");
        }

        cache.get("key1");

        cache.put("key4", "value4");

        Assert.assertNull(cache.get("key1"));
        Assert.assertEquals("value2", cache.get("key2"));
        Assert.assertEquals("value3", cache.get("key3"));
        Assert.assertEquals("value4", cache.get("key4"));
    }

    @Test
    public void testTimeWheel() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(3).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.TIME_WHEEL_EXPIRE)
                .cacheEvict(LFU)
                .noPersist()
                .build();

        cache.put("key1", "value1", 500);
        cache.put("key2", "value2", 1000);
        cache.put("key3", "value3", 1000);
        cache.put("key4", "value4", 61000);

        Thread.sleep(1000);

        Assert.assertNull(cache.get("key1"));
        Assert.assertNull(cache.get("key2"));
        Assert.assertNull(cache.get("key3"));
        Assert.assertEquals("value4", cache.get("key4"));
        Thread.sleep(60100);
        Assert.assertNull(cache.get("key4"));
    }

    @Test
    public void testPersistRDB() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(3).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.TIME_WHEEL_EXPIRE)
                .cacheEvict(LFU)
                .cachePersist(CachePersistConstant.RDB_PERSIST, CachePersistConstant.RDB_TEST_PERIOD, CachePersistConstant.DEFAULT_RDB_PATH)
                .build();

        cache.put("key1", "value1", 4000);
        cache.put("key2", "value2", 12000);
        cache.put("key3", "value3", 22000);
        cache.put("key4", "value4", 6000);

        Thread.sleep(2100);
        cache.put("key5", "value5", 6000);
        cache.put("key1", "value6", 6000);
        cache.put("key7", "value7");
        Thread.sleep(2100);

        ICache<String, String> newCache = cacheBuilder.capacity(3).map(new HashMap<>())
                .cacheExpire(CacheExpireConstant.TIME_WHEEL_EXPIRE)
                .cacheEvict(LRU)
                .cachePersist(CachePersistConstant.RDB_PERSIST, CachePersistConstant.RDB_TEST_PERIOD, CachePersistConstant.DEFAULT_RDB_PATH)
                .cacheLoader(RDB_LOAD, "1.rdb")
                .build();

        Assert.assertEquals("value6", newCache.get("key1"));
        Assert.assertEquals("value5", newCache.get("key5"));
        Assert.assertNull(newCache.getExpireStrategy().expireTime("key7"));
    }

    @Test
    public void testCMS() throws InterruptedException {
        CMS<String> cms = new CMS<>();
        cms.ensureCapacity(100L);
        cms.increment("nihao");
    }

    @Test
    public void testWTinyLFUEvict() throws InterruptedException {
        CacheBuilder<String, String> cacheBuilder = new CacheBuilder<>();
        ICache<String, String> cache = cacheBuilder.capacity(100).map(new HashMap<>())
                .slowListener(new CacheSlowListener<>())
                .cacheEvict(CacheEvictConstant.W_TINY_LFU)
                .build();

        WTinyLFUCacheEvict evict = (WTinyLFUCacheEvict) cache.getEvictStrategy();
        for(int i = 0; i < 101; i++){
            cache.put("key" + i, "value" + i);
        }

        evict = (WTinyLFUCacheEvict) cache.getEvictStrategy();
        for (int i = 0; i < 101; i++) {
            for (int j = 0; j < 7; j++) {
                cache.get("key" + i);
            }
        }

        evict = (WTinyLFUCacheEvict) cache.getEvictStrategy();
    }



}
