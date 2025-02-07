package top.brightsunshine.localcache.cacheInterface;

import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

/**
 * 拦截器的接口
 */
public interface ICacheInterceptor<K, V> {
    /**
     * 被拦截的方法执行之前执行
     */
    public void before(CacheInterceptorContext<K, V> context);

    /**
     * 被拦截的方法执行之后执行
     */
    public void after(CacheInterceptorContext<K, V> context);
}
