package com.isoops.basicmodule.classes.annotation;



import com.isoops.basicmodule.classes.annotation.source.CheckGradeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CheckRequest {

    /**
     * 是否为URL
     */
    boolean isUrl() default false;

    /**
     * 校验等级
     */
    CheckGradeEnum level() default CheckGradeEnum.SIGN_CHECK;

    /**
     * 接口请求锁-请求间隔
     */
    long seconds() default 1;

    /**
     * 接口请求锁-请求间隔内-最大请求数
     */
    long maxCount() default 1;
}
