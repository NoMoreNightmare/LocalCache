package top.brightsunshine.localcache.core.proxy.icacheproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.proxy.CacheProxy;
import top.brightsunshine.localcache.core.proxy.CacheProxyHelper;
import top.brightsunshine.localcache.core.proxy.ICacheProxy;
import top.brightsunshine.localcache.core.proxy.context.ICacheProxyContext;
import top.brightsunshine.localcache.core.proxy.context.impl.CacheProxyContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor, ICacheProxy {

    private ICache target;

    public CglibProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        ICacheProxyContext context = CacheProxyContext.getInstance()
                .method(method)
                .target(target)
                .args(objects);
        return CacheProxyHelper.getInstance().cacheProxyContext(context).execute();
    }

    @Override
    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);

        return enhancer.create();
    }
}
