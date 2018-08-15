package com.redissouce.redisBean;

import java.io.Serializable;

/**
 * Created by samuel on 2017/9/28.
 */
public class RequestClientBean implements Serializable {


    private Integer creatCount;
    private Integer editCount;

    public Integer getCreatCount() {
        return creatCount;
    }

    public void setCreatCount(Integer creatCount) {
        this.creatCount = creatCount;
    }

    public Integer getEditCount() {
        return editCount;
    }

    public void setEditCount(Integer editCount) {
        this.editCount = editCount;
    }
}
