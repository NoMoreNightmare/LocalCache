package top.brightsunshine.localcache.core.interceptor.evict;

import top.brightsunshine.localcache.cacheInterface.ICacheEvict;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.core.entry.CacheEntry;
import top.brightsunshine.localcache.core.evict.WTinyLFUCacheEvict;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;
import top.brightsunshine.localcache.cacheInterface.ICacheRemoveListener;

import java.lang.reflect.Method;
import java.util.Map;

import static top.brightsunshine.localcache.core.listener.remove.CacheRemoveConstant.REMOVE_EVICT;

public class CacheEvictInterceptor<K, V> implements ICacheInterceptor<K, V> {
    @Override
    public void before(CacheInterceptorContext<K, V> context) {
        Method method = context.getMethod();
        Object[] args = context.getArgs();
        ICacheRemoveListener<K, V> listener = context.getRemoveListener();

        ICacheEvict<K, V> evictStrategy = context.getCache().getEvictStrategy();

        if("put".equals(method.getName())) {
            CacheEntry<K, V> entry = evictStrategy.evict((K) args[0]);
            if(entry != null) {
                listener.listen(entry.getKey(), entry.getValue(), REMOVE_EVICT);
            }
        }else if("putAll".equals(method.getName())) {
            Map<K, V> map = (Map<K, V>) args[0];
            for (K key : map.keySet()) {
                CacheEntry<K, V> entry = evictStrategy.evict(key);
                if(entry != null) {
                    listener.listen(entry.getKey(), entry.getValue(), REMOVE_EVICT);
                }
            }
        }
    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {
        //获取当前cache的内存淘汰策略
        ICacheEvict<K, V> evictStrategy = context.getCache().getEvictStrategy();

        //获取被拦截的方法
        Method method = context.getMethod();
        ICacheRemoveListener<K, V> listener = context.getRemoveListener();

        //获取被拦截的方法的第一个参数：注意key在参数中的顺序
        if("putAll".equals(method.getName())) {
            Map<K, V> kvPair = (Map<K, V>) context.getArgs()[0];
            for(K key : kvPair.keySet()) {
                CacheEntry<K, V> entry = evictStrategy.updateStatus(key);
                if(context.getCache() instanceof WTinyLFUCacheEvict){
                    if(entry != null) {
                        listener.listen(entry.getKey(), entry.getValue(), REMOVE_EVICT);
                    }
                }
            }
        }else if("remove".equals(method.getName())) {
            K key = (K) context.getArgs()[0];
            evictStrategy.deleteKey(key);
        }else{
            K key = (K) context.getArgs()[0];
            CacheEntry<K, V> entry = evictStrategy.updateStatus(key);
            //WTinyLFU的驱逐过程可能发生在更新key期间
            if(context.getCache().getEvictStrategy() instanceof WTinyLFUCacheEvict){
                if(entry != null) {
                    listener.listen(entry.getKey(), entry.getValue(), REMOVE_EVICT);
                }
            }
        }
    }
}
