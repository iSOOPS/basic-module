package com.elasticsearch.query;


import com.elasticsearch.SESEnum;

import java.io.Serializable;

/**
 * Created by Samuel on 2016/11/8.
 */
public class SElasticTerm<T> implements Serializable{
    /**
     * -注释-
     *
     * -使用场景-
     * 用于查询全文中 特定字段 特定value 的所有数据条目
     * 栗子:查询所有字段为"info" 且数据为 value字样的数据条目
     *
     * -使用范围-
     *  type控制
     *  must:获取字段为key且其值满足value数据条目
     *  should:获取字段为key且其值满足value数据条目(也可以不满足)
     *  mustNot:获取字段为key且其不满足value的数据条目
     *
     *  isPhrase控制（要求顺序）
     *  true 精确匹配
     *  栗子:目标数据"我们在一起",存在数据A:"我们不在一起"、B:"可能我们在一起",B将会被搜索出来,A不会
     *  false 非精确匹配
     *  栗子:目标数据"我们在一起",存在数据A:"我们不在一起"、B:"可能我们在一起",AB都会被搜索出来
     *
     */
    public String[] keys;//多key
    public Object value;//需要查询的数据

    public boolean isPhrase;//是否精确匹配（默认false）
    public SESEnum type;//查询是非条件

    public SElasticTerm(boolean isPhrase,SESEnum type,Object value,String ...arg){
        this.isPhrase = isPhrase;
        this.type = type;
        this.value = value;
        this.keys = arg;
    }



    public String[] getKeys() {
        return keys;
    }

    public Object getValue() {
        return value;
    }

    public boolean isPhrase() {
        return isPhrase;
    }

    public SESEnum getType() {
        return type;
    }
}
