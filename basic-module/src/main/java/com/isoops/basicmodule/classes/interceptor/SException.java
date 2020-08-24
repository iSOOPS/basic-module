package com.isoops.basicmodule.classes.interceptor;

import com.isoops.basicmodule.classes.basicmodel.GenericEnum;

public class SException extends Exception {

    //异常拦截对象

    private GenericEnum genericEnum;

    public SException() {
        super("unkown error");
    }

    public SException(GenericEnum genericEnum) {
        super(genericEnum.getReasonPhrase());
        this.setGenericEnum(genericEnum);
    }


    public GenericEnum getGenericEnum() {
        return genericEnum;
    }

    public void setGenericEnum(GenericEnum genericEnum) {
        this.genericEnum = genericEnum;
    }
}
