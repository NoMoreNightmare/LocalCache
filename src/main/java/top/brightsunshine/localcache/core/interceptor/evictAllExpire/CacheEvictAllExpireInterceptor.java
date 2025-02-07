package top.brightsunshine.localcache.core.interceptor.evictAllExpire;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICacheExpire;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

public class CacheEvictAllExpireInterceptor<K, V> implements ICacheInterceptor<K, V>{
    @Override
    public void before(CacheInterceptorContext<K, V> context) {
        //获取当前的cacheExpire策略
        ICacheExpire<K, V> cacheExpire = context.getCache().cacheExpire();
        if("clear".equals(context.getMethod().getName())){
            cacheExpire.deleteAllKeys();
        }else{
            cacheExpire.lazyDeleteAllExpiredKeys();
        }

    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {

    }
}
