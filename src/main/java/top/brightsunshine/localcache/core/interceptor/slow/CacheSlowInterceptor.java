package top.brightsunshine.localcache.core.interceptor.slow;

import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICacheSlowListener;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

import java.util.List;

public class CacheSlowInterceptor<K, V> implements ICacheInterceptor<K, V> {
    @Override
    public void before(CacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {
        List<ICacheSlowListener<K, V>> slowListeners = context.getSlowListeners();
        for (ICacheSlowListener<K, V> slowListener : slowListeners) {
            long slowerThan = slowListener.slowerThan();
            if(slowerThan <= (context.getEndTime() - context.getStartTime())) {
                slowListener.listen(context);
            }
        }

    }
}
