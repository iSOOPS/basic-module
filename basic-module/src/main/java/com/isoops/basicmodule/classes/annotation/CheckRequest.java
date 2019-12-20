package com.isoops.basicmodule.classes.annotation;



import com.isoops.basicmodule.classes.sign.CheckGradeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CheckRequest {

    /**
     * 是否暂停使用
     */
    boolean ignore() default false;

    /**
     * 校验等级
     */
    CheckGradeEnum level() default CheckGradeEnum.signCheck;
}
