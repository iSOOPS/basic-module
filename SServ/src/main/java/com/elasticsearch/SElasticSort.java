package com.elasticsearch;

import java.io.Serializable;

/**
 * Created by Samuel on 2016/11/10.
 */
public class SElasticSort implements Serializable{
    /**
     * -注释-
     *
     * -使用场景-
     * 用于将查询出来的数据根据某个字段进行排序
     * 栗子:查询所有数据更具字段为"age" 进行 由高到低进行排序
     *
     * -使用范围-
     *  isASC_DESC控制
     *  反序(大到小) isASC_DESC控制==false
     *  正序(小到大) isASC_DESC控制==true
     */
    public String key;//查询的字段名称
    public boolean isASC_DESC;//需要查询的数据

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isASC_DESC() {
        return isASC_DESC;
    }

    public void setASC_DESC(boolean ASC_DESC) {
        isASC_DESC = ASC_DESC;
    }
}
