package top.brightsunshine.localcache.core.interceptor.context;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheRemoveListener;
import top.brightsunshine.localcache.cacheInterface.ICacheSlowListener;

import java.lang.reflect.Method;

/**
 * 拦截器需要的对象信息组成的上下文
 */
public class CacheInterceptorContext<K, V> {
    private ICache<K, V> cache;

    private Method method;
    private Object[] args;

    private Object result;

    private ICacheRemoveListener<K, V> removeListener;

    private ICacheSlowListener<K, V> slowListener;

    private long startTime;

    private long endTime;

    public static <K,V> CacheInterceptorContext<K,V> getInstance() {
        return new CacheInterceptorContext<>();
    }

    public CacheInterceptorContext<K, V> removeListener(ICacheRemoveListener<K, V> listener) {
        this.removeListener = listener;
        return this;
    }

    public ICacheRemoveListener<K, V> getRemoveListener() {
        return removeListener;
    }

    public CacheInterceptorContext<K, V> slowListener(ICacheSlowListener<K, V> listener) {
        this.slowListener = listener;
        return this;
    }

    public ICacheSlowListener<K, V> getSlowListener() {
        return slowListener;
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

    public long getStartTime() {
        return startTime;
    }

    public CacheInterceptorContext<K, V> startTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getEndTime() {
        return endTime;
    }

    public CacheInterceptorContext<K, V> endTime(long endTime) {
        this.endTime = endTime;
        return this;
    }
}
