package com.isoops.basicmodule.classes.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerLock {

    /**接口请求锁-防止暴力请求/重复请求/最大请求数**/

    /**
     * 请求间隔
     */
    long seconds() default 1;

    /**
     * 请求间隔内-最大请求数
     */
    long maxCount() default 1;

    /**
     * 是否需要登录
     */
    boolean needLogin() default true;
}
