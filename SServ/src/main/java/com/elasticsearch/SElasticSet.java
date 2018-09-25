package com.elasticsearch;

import java.io.Serializable;

public class SElasticSet<T> implements Serializable {

    private String key;
    private T object;

    public boolean checkData(){
        if (key == null || object == null){
            return false;
        }
        return true;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
