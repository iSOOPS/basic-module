package com.elasticsearch;

import java.io.Serializable;

/**
 * Created by Samuel on 2016/11/8.
 */
public class SElasticRange implements Serializable{
    /**
     * -注释-
     *
     * -使用场景-
     * 用于查询特定范围的数据条目
     * 栗子 价格为199-500的所有商品数据
     *
     * -使用范围-
     *  type控制
     *  must:获取必须满足范围条件的数据
     *  should:获取可以满足范围条件(可以不满足)的数据
     *  mustNot:获取必须在范围条件以外的数据
     */
    public String key;//查询字段名称
    public String from;//其实范围（从0开始）
    public String to;//结束范围

    public SESEnum type;//查询是非条件

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public SESEnum getType() {
        return type;
    }

    public void setType(SESEnum type) {
        this.type = type;
    }
}
