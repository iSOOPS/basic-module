package com.elasticsearch;

import java.io.Serializable;

/**
 * Created by Samuel on 2016/11/9.
 */
public class SElasticSingle implements Serializable{
    /**
     * -注释-
     *
     * -使用场景-
     * 用于查询全文中包含 特定value 的所有数据条目
     * 栗子:查询所有字段数据包含"牛仔"字样的数据条目（可以不同字段）
     *
     * -使用范围-
     *  type控制
     *  must:获取必须存在value数据条目
     *  should:获取存在value的数据条目(也可以不存在)
     *  mustNot:获取不存在value的数据条目
     */
    public String value;//需要查询的字段

    public SESEnum type;//查询是非条件

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SESEnum getType() {
        return type;
    }

    public void setType(SESEnum type) {
        this.type = type;
    }
}
