package com.isoops.basicmodule.classes.annotation;


import com.isoops.basicmodule.classes.annotation.source.LoggerEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Logger {

    LoggerEnum logType() default LoggerEnum.ALL;

    String msg() default "controller";
}
