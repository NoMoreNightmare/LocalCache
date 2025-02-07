package top.brightsunshine.localcache.core.proxy.context;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICache;

import java.lang.reflect.Method;

/**
 * 代理对象需要的各种对象信息
 */
public interface ICacheProxyContext {

    public ICacheProxyContext target(ICache target);

    public ICache target();

    public ICacheProxyContext method(Method method);

    public Method method();

    public ICacheProxyContext args(Object[] args);

    public Object[] args();

    public CacheInterceptor interceptor();


}
