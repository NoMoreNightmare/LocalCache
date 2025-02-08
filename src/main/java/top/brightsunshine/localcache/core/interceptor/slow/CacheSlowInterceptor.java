package top.brightsunshine.localcache.core.interceptor.slow;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICacheSlowListener;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

public class CacheSlowInterceptor<K, V> implements ICacheInterceptor<K, V> {
    @Override
    public void before(CacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {
        ICacheSlowListener<K, V> slowListener = context.getSlowListener();
        if (slowListener != null) {
            long slowerThan = slowListener.slowerThan();
            if(slowerThan <= (context.getEndTime() - context.getStartTime())) {
                slowListener.listen(context);
            }
        }
    }
}
