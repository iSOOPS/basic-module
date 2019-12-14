package com.isoops.basicmodule.classes.annotation.source;

public class InterceptorException extends Exception {

    //异常拦截对象

    public InterceptorException() {
        super("unkown error");
    }

    public InterceptorException(String message) {
        super(message);
    }
}
