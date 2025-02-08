package top.brightsunshine.localcache.annotation;

import top.brightsunshine.localcache.core.constant.CachePersistConstant;

import java.lang.annotation.*;

/**
 * aop拦截注解
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheInterceptor {
    boolean evict() default false;

    boolean evictAllExpired() default false;

    boolean persist() default false;

    boolean slow() default false;
}
