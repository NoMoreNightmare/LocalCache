package top.brightsunshine.localcache.core.proxy.dynamic;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.proxy.CacheProxyHelper;
import top.brightsunshine.localcache.core.proxy.ICacheProxy;
import top.brightsunshine.localcache.core.proxy.context.ICacheProxyContext;
import top.brightsunshine.localcache.core.proxy.context.impl.CacheProxyContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代理
 */
public class DynamicProxy implements InvocationHandler, ICacheProxy {
    /**
     * 被代理对象
     */
    private ICache target;

    public DynamicProxy(ICache target) {
        this.target = target;
    }

    /**
     * 拦截并执行相应的方法
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ICacheProxyContext context = CacheProxyContext.getInstance()
                .method(method)
                .args(args)
                .target(target);

        //执行拦截方法，并返回可能存在的结果
        return CacheProxyHelper.getInstance().cacheProxyContext(context).execute();
    }

    /**
     * 创建代理对象
     * @return
     */
    @Override
    public Object proxy() {
        InvocationHandler handler = new DynamicProxy(target);
        return Proxy.newProxyInstance(handler.getClass().getClassLoader(), handler.getClass().getInterfaces(), handler);
    }
}
