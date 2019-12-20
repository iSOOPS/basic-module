package com.isoops.basicmodule.classes.annotation;



import com.isoops.basicmodule.classes.sign.CheckGradeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CheckRequestUrl {

    /**
     * 是否暂停使用
     */
    boolean ignore() default false;

    /**
     * 需要解析的签名Code字段名称
     */
    String codeKey() default "code";

    /**
     * 需要解析的签名sign字段名称
     */
    String signKey() default "sign";

    /**
     * 需要解析的签名用户字段名称
     */
    String userSignalKey() default "userSignal";

    /**
     * 校验等级 codeCheck只校验Code/signCheck均校验code和sign
     */
    CheckGradeEnum level() default CheckGradeEnum.signCheck;
}
