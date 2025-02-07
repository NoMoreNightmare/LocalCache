package top.brightsunshine.localcache.annotation;

import java.lang.annotation.*;

/**
 * aop拦截注解
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheInterceptor {
}
