package com.elasticsearch;


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
     *  isPhrase控制
     *  true 精确匹配
     *  栗子:目标数据"我们在一起",存在数据A:"我们不在一起"、B:"可能我们在一起",B将会被搜索出来,A不会
     *  false 非精确匹配
     *  栗子:目标数据"我们在一起",存在数据A:"我们不在一起"、B:"可能我们在一起",AB都会被搜索出来
     *
     */
    public String key;//查询的字段名称
    public Object value;//需要查询的数据

    public boolean isPhrase;//是否精确匹配（默认false）
    public SESEnum type;//查询是非条件

    public boolean isMulti;//多字段匹配一个values 满足其一即可 true- 使用keys
    public String[] keys;//多key

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isPhrase() {
        return isPhrase;
    }

    public void setPhrase(boolean phrase) {
        isPhrase = phrase;
    }

    public SESEnum getType() {
        return type;
    }

    public void setType(SESEnum type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
