package com.elasticsearch.query;

import com.elasticsearch.SESEnum;

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
    public Integer from;//其实范围（从0开始）
    public Integer to;//结束范围

    public SESEnum type;//查询是非条件

    public SElasticRange(String key,Integer from,Integer to,SESEnum type){
        this.key = key;
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public Integer getFrom() {
        return Integer.valueOf(from);
    }

    public Integer getTo() {
        return Integer.valueOf(to);
    }

    public SESEnum getType() {
        return type;
    }
}
