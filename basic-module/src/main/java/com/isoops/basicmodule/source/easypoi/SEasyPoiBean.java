package com.isoops.basicmodule.source.easypoi;

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
