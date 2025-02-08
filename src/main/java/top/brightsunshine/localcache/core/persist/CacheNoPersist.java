package top.brightsunshine.localcache.core.persist;

import top.brightsunshine.localcache.cacheInterface.ICachePersist;

import java.util.concurrent.TimeUnit;

public class CacheNoPersist<K, V> implements ICachePersist<K, V> {
    @Override
    public void persist() {

    }

    @Override
    public int delay() {
        return 0;
    }

    @Override
    public int period() {
        return 0;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}
