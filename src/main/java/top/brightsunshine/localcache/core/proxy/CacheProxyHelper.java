package top.brightsunshine.localcache.core.proxy;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.core.interceptor.CacheInterceptorUtil;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;
import top.brightsunshine.localcache.core.interceptor.persist.CachePersistAOFInterceptor;
import top.brightsunshine.localcache.core.persist.CachePersistAOF;
import top.brightsunshine.localcache.core.proxy.context.ICacheProxyContext;

import java.lang.reflect.InvocationTargetException;

public class CacheProxyHelper {
    /**
     * 包含cache所需信息的上下文
     */
    private ICacheProxyContext cacheProxyContext;

    /**
     * 从CacheInterceptor中初始化使用的evict拦截器
     */
    private ICacheInterceptor cacheEvictInterceptor = CacheInterceptorUtil.cacheEvictInterceptor();

    /**
     * 获取的移除所有过期key的拦截器
     */
    private ICacheInterceptor cacheEvictAllExpireInterceptor = CacheInterceptorUtil.cacheEvictAllExpireInterceptor();

    /**
     * 持久化拦截器
     */
    private ICacheInterceptor cachePersistInterceptor = CacheInterceptorUtil.cachePersistInterceptor(null);


    private ICacheInterceptor cacheSlowInterceptor = CacheInterceptorUtil.cacheSlowInterceptor();

    public static CacheProxyHelper getInstance() {
        return new CacheProxyHelper();
    }

    public CacheProxyHelper cacheProxyContext(ICacheProxyContext cacheProxyContext) {
        this.cacheProxyContext = cacheProxyContext;
        this.cachePersistInterceptor = CacheInterceptorUtil.cachePersistInterceptor(cacheProxyContext.target().getPersistStrategy());
        return this;
    }


    /**
     * 拦截后执行的代码
     * @return
     */
    public Object execute() throws InvocationTargetException, IllegalAccessException {
        ICache cache = cacheProxyContext.target();
        CacheInterceptor interceptor = cacheProxyContext.interceptor();

        //创建拦截器interceptor需要的上下文
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.getInstance()
                .method(cacheProxyContext.method())
                .args(cacheProxyContext.args())
                .cache(cache)
                .removeListener(cache.getRemoveListeners())
                .slowListeners(cache.getSlowListeners())
                .startTime(System.currentTimeMillis());

        if(interceptor != null) {
            if(interceptor.slow()){
                cacheSlowInterceptor.before(interceptorContext);
            }

            //TODO 执行所有拦截器的before方法
            if(interceptor.evict()){
                cacheEvictInterceptor.before(interceptorContext);
            }

            if(interceptor.evictAllExpired()){
                cacheEvictAllExpireInterceptor.before(interceptorContext);
            }

            if(interceptor.persist()){
                cachePersistInterceptor.before(interceptorContext);
            }

        }

        Object result = null;
        //执行原始方法
        if(cachePersistInterceptor instanceof CachePersistAOFInterceptor){
            ((CachePersistAOF)cache.getPersistStrategy()).lock();
            result = cacheProxyContext.invokeOrigin();
            ((CachePersistAOF)cache.getPersistStrategy()).unlock();
        }else{
            result = cacheProxyContext.invokeOrigin();
        }


        interceptorContext.endTime(System.currentTimeMillis());

        if(interceptor != null) {
            //执行所有拦截器的after方法
            if(interceptor.slow()){
                cacheSlowInterceptor.after(interceptorContext);
            }
            if(interceptor.evict()){
                cacheEvictInterceptor.after(interceptorContext);
            }

            if(interceptor.evictAllExpired()){
                cacheEvictAllExpireInterceptor.after(interceptorContext);
            }

            if(interceptor.persist()){
                cachePersistInterceptor.after(interceptorContext);
            }
        }


        return result;
    }
}
