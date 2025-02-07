package top.brightsunshine.localcache.core.proxy.context.impl;

import top.brightsunshine.localcache.annotation.CacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.proxy.context.ICacheProxyContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CacheProxyContext implements ICacheProxyContext {
    /**
     * 被代理对象
     */

    private ICache target;

    /**
     * 被调用的方法
     */
    private Method method;

    /**
     * 方法参数
     */
    private Object[] args;

    /**
     * 拦截器注解
     */
    private CacheInterceptor interceptor;

    public static ICacheProxyContext getInstance() {
        return new CacheProxyContext();
    }

    @Override
    public ICacheProxyContext target(ICache target) {
        this.target = target;
        return this;
    }

    @Override
    public ICache target() {
        return target;
    }

    @Override
    public ICacheProxyContext method(Method method) {
        this.method = method;
        this.interceptor = method.getAnnotation(CacheInterceptor.class);
        return this;
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public ICacheProxyContext args(Object[] args) {
        this.args = args;
        return this;
    }

    @Override
    public Object[] args() {
        return args;
    }

    @Override
    public CacheInterceptor interceptor() {
        return interceptor;
    }

    @Override
    public Object invokeOrigin() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }
}
