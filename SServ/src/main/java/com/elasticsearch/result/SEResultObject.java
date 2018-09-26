package com.elasticsearch.result;

import java.io.Serializable;

/**
 * Created by Samuel on 2017/1/17.
 */
public class SEResultObject<T> extends SEResult{

    private T object;

    public SEResultObject(Boolean isok){
        super(isok);
        this.object = null;
    }

    public SEResultObject(String msg){
        super(msg);
        this.object = null;
    }

    public SEResultObject(T object){
        super(true);
        this.object = object;
    }

    public SEResultObject(Long dataCount,T object){
        super(dataCount);
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
