package top.brightsunshine.localcache.core.proxy;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.core.proxy.icacheproxy.CglibProxy;
import top.brightsunshine.localcache.core.proxy.icacheproxy.DynamicProxy;

import java.lang.reflect.Proxy;

/**
 * 创建ICache的代理对象，以实现AOP功能
 */
public class CacheProxy {

    public static ICache getProxy(ICache cache) {
        if(cache == null){
            throw new NullPointerException("cache is null");
        }

        final Class cacheClass = cache.getClass();

        if(cacheClass.isInterface() || Proxy.isProxyClass(cacheClass)){
            return (ICache) new DynamicProxy(cache).proxy();
        }

        return (ICache) new CglibProxy(cache).proxy();
    }
}
