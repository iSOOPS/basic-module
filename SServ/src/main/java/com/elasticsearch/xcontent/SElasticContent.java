package com.elasticsearch.xcontent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SElasticContent {

    public SElasticContent(){

    }

    public SElasticContent(SContentBuilder...arg){
        this.list = Arrays.asList(arg);
    }

    private List<SContentBuilder> list;

    public List<SContentBuilder> getList() {
        return list;
    }

    public void setList(List<SContentBuilder> list) {
        this.list = list;
    }
}
