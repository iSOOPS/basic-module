package com.elasticsearch;

import java.io.Serializable;

/**
 * Created by Samuel on 2017/1/17.
 */
public class SEResultObject<T> implements Serializable{

    private Boolean state;
    private String msg;
    private T object;
    private Integer stateCode;
    private Long dataCount;//查询数据总数量



    public SEResultObject(Boolean isok){
        changeState(isok);
        this.object = null;
    }

    public SEResultObject(String msg){
        changeState(false);
        this.object = null;
        this.msg = msg;
    }

    public SEResultObject(T object){
        changeState(true);
        this.object = object;
    }

    public SEResultObject(Long dataCount,T object){
        changeState(true);
        this.object = object;
        this.dataCount = dataCount;
    }


    private void changeState(Boolean bools)
    {
        if (bools)
        {
            this.state = true;
            if (this.msg==null || this.msg.length()<1){
                this.msg = "成功";
            }
            this.stateCode = 200;
        }
        else
        {
            this.state = false;
            if (this.msg==null || this.msg.length()<1){
                this.msg = "失败";
            }
            this.stateCode = 400;
        }
    }






    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
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
