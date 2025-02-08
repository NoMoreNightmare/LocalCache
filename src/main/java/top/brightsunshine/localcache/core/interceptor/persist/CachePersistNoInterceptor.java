package top.brightsunshine.localcache.core.interceptor.persist;

import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

public class CachePersistNoInterceptor<K, V> implements ICacheInterceptor<K, V> {
    @Override
    public void before(CacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {

    }
}
