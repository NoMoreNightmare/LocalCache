package top.brightsunshine.localcache.cacheInterface;

import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

public interface ICacheSlowListener<K, V> {
    void listen(CacheInterceptorContext<K, V> context);

    long slowerThan();
}
