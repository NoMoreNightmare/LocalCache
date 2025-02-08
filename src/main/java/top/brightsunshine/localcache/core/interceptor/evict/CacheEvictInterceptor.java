package top.brightsunshine.localcache.core.interceptor.evict;

import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

import java.lang.reflect.Method;
import java.util.Map;

public class CacheEvictInterceptor<K, V> implements ICacheInterceptor<K, V> {
    @Override
    public void before(CacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {
        //获取当前cache的内存淘汰策略
        ICacheEvict<K, V> evictStrategy = context.getCache().getEvictStrategy();

        //获取被拦截的方法
        Method method = context.getMethod();

        //获取被拦截的方法的第一个参数：注意key在参数中的顺序
        if("putAll".equals(method.getName())) {
            Map<K, V> kvPair = (Map<K, V>) context.getArgs()[0];
            for(K key : kvPair.keySet()) {
                evictStrategy.updateStatus(key, context.getCache());
            }
        }else if("remove".equals(method.getName())) {
            K key = (K) context.getArgs()[0];
            evictStrategy.deleteKey(key, context.getCache());
        }else{
            K key = (K) context.getArgs()[0];
            evictStrategy.updateStatus(key, context.getCache());
        }
    }
}
