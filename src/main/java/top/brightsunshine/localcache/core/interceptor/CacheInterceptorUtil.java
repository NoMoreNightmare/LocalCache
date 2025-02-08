package top.brightsunshine.localcache.core.interceptor;

import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICachePersist;
import top.brightsunshine.localcache.core.entry.AofPersistEntry;
import top.brightsunshine.localcache.core.evict.LRUCacheEvict;
import top.brightsunshine.localcache.core.interceptor.evict.CacheEvictInterceptor;
import top.brightsunshine.localcache.core.interceptor.evictAllExpire.CacheEvictAllExpireInterceptor;
import top.brightsunshine.localcache.core.interceptor.persist.CachePersistAOFInterceptor;
import top.brightsunshine.localcache.core.interceptor.persist.CachePersistNoInterceptor;
import top.brightsunshine.localcache.core.persist.CachePersistAOF;

public class CacheInterceptorUtil {

    public static ICacheInterceptor cacheEvictInterceptor() {
        return new CacheEvictInterceptor();
    }

    public static ICacheInterceptor cacheEvictAllExpireInterceptor() {
        return new CacheEvictAllExpireInterceptor();
    }

    public static ICacheInterceptor cachePersistInterceptor(ICachePersist cachePersist) {
        if(cachePersist instanceof CachePersistAOF){
            return new CachePersistAOFInterceptor();
        }
        return new CachePersistNoInterceptor();
    }

}
