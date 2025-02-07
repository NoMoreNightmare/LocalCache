package top.brightsunshine.localcache.core.proxy;

import top.brightsunshine.localcache.core.proxy.context.ICacheProxyContext;

public class CacheProxyHelper {
    /**
     * 包含cache所需信息的上下文
     */
    private ICacheProxyContext cacheProxyContext;

    public static CacheProxyHelper getInsance() {
        return new CacheProxyHelper();
    }

    public CacheProxyHelper cacheProxyContext(ICacheProxyContext cacheProxyContext) {
        this.cacheProxyContext = cacheProxyContext;
        return this;
    }


    /**
     * 拦截后执行的代码
     * @return
     */
    public Object execute() {
        return null;
    }
}
