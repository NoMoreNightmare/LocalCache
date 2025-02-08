package top.brightsunshine.localcache.core.interceptor.context;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.listener.ICacheRemoveListener;

import java.lang.reflect.Method;

/**
 * 拦截器需要的对象信息组成的上下文
 */
public class CacheInterceptorContext<K, V> {
    private ICache<K, V> cache;

    private Method method;
    private Object[] args;

    private Object result;

    private ICacheRemoveListener<K, V> listener;

    public static <K,V> CacheInterceptorContext<K,V> getInstance() {
        return new CacheInterceptorContext<>();
    }

    public CacheInterceptorContext<K, V> removeListener(ICacheRemoveListener<K, V> listener) {
        this.listener = listener;
        return this;
    }

    public ICacheRemoveListener<K, V> getRemoveListener() {
        return listener;
    }

    public ICache<K, V> getCache() {
        return cache;
    }

    public CacheInterceptorContext<K, V> cache(ICache<K, V> cache) {
        this.cache = cache;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public CacheInterceptorContext<K, V> method(Method method) {
        this.method = method;
        return this;
    }

    public Object[] getArgs() {
        return args;
    }

    public CacheInterceptorContext<K, V> args(Object[] args) {
        this.args = args;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public CacheInterceptorContext<K, V> result(Object result) {
        this.result = result;
        return this;
    }

}
