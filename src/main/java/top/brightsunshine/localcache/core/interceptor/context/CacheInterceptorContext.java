package top.brightsunshine.localcache.core.interceptor.context;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheRemoveListener;
import top.brightsunshine.localcache.cacheInterface.ICacheSlowListener;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 拦截器需要的对象信息组成的上下文
 */
public class CacheInterceptorContext<K, V> {
    private ICache<K, V> cache;

    private Method method;
    private Object[] args;

    private Object result;

    private List<ICacheRemoveListener<K, V>> removeListeners;

    private List<ICacheSlowListener<K, V>> slowListeners;

    private long startTime;

    private long endTime;

    public static <K,V> CacheInterceptorContext<K,V> getInstance() {
        return new CacheInterceptorContext<>();
    }

    public CacheInterceptorContext<K, V> removeListener(List<ICacheRemoveListener<K, V>> listener) {
        this.removeListeners = listener;
        return this;
    }

    public List<ICacheRemoveListener<K, V>> getRemoveListeners() {
        return removeListeners;
    }

    public CacheInterceptorContext<K, V> slowListeners(List<ICacheSlowListener<K, V>> listeners) {
        this.slowListeners = listeners;
        return this;
    }

    public List<ICacheSlowListener<K, V>> getSlowListeners() {
        return slowListeners;
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
