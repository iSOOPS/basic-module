package com.redissouce;

import java.io.Serializable;

/**
 * Created by samuel on 2017/9/7.
 */
public class RedisBean<T> implements Serializable {
    private Boolean state;
    private String msg;
    private T object;         //数据对象

    public RedisBean(Boolean isOk) {
        changStatus(isOk);
    }
    public void changStatus(Boolean isOk){
        this.state = isOk;
        if (isOk){
            this.msg = "success";
        }
        else {
            this.msg = "fail";
        }
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
