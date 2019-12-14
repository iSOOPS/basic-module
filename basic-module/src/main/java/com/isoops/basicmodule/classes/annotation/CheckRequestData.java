package com.isoops.basicmodule.classes.annotation;



import com.isoops.basicmodule.classes.sign.CheckGradeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CheckRequestData {

    /**
     * 校验等级
     */
    CheckGradeEnum level() default CheckGradeEnum.signCheck;
}
