package top.brightsunshine.localcache.cacheInterface;

import java.util.concurrent.TimeUnit;

public interface ICachePersist<K, V> {
    public void persist();

    public int delay();
    public int period();

    public TimeUnit timeUnit();
}
