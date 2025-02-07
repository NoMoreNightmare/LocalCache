package top.brightsunshine.localcache.core.interceptor;

import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.core.evict.LRUCacheEvict;
import top.brightsunshine.localcache.core.interceptor.evict.CacheEvictInterceptor;
import top.brightsunshine.localcache.core.interceptor.evictAllExpire.CacheEvictAllExpireInterceptor;

public class CacheInterceptorUtil {

    public static ICacheInterceptor cacheEvictInterceptor() {
        return new CacheEvictInterceptor();
    }

    public static ICacheInterceptor cacheEvictAllExpireInterceptor() {
        return new CacheEvictAllExpireInterceptor();
    }
}
