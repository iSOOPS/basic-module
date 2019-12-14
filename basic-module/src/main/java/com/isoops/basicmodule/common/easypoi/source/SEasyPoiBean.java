package com.isoops.basicmodule.common.easypoi.source;

import lombok.Data;

@Data
public class SEasyPoiBean {

    private String keyName;
    private String key;

    public SEasyPoiBean(String keyName, String key){
        this.keyName = keyName;
        this.key = key;
    }
}
