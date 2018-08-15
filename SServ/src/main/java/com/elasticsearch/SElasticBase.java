package com.elasticsearch;


import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Samuel on 2016/11/8.
 */
public class SElasticBase implements Serializable{

    /******** 基本参数 **********/
    public String index;//索引
    public String type;//类型
    public String id;//对象id

    /******** 增量对象 **********/
    private String jsonMap;//数据对象

    /******** 查询分页 **********/
    public int pageIndex;//分页-下标
    public int pageSize;//分页-范围

    /******** 查询条件 **********/
    public List<SElasticSingle> singleList;//全文内容 单string查询（ps:没有key）
    public List<SElasticTerm> terms;//全文内容 特定字段key-value查询
    public List<SElasticRange> ranges;//全文内容 范围查询 数组 （ps:被查询的参数必须为-数字类型，如果为字符类型则为匹配）

    /******** 显示条件 **********/
    public List<String> fields;//高亮字段 数组
    public List<SElasticSort> sorts;//排序条件 数组

    /******** jsonMap **********/
    public String getJsonMap() {
        return jsonMap;
    }
    public void setJsonMap(Map<String,Object> map) {
        String jsonString = JSON.toJSONString(map);
        this.jsonMap = jsonString;
    }

    /******** check **********/
    public boolean checkNullPrivate(){
        if (this.id==null ||this.index==null||this.type==null){
            return false;
        }
        return true;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        String checkIndex = index.replace("#","").toLowerCase();
        this.index = checkIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJsonMap(String jsonMap) {
        this.jsonMap = jsonMap;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<SElasticSingle> getSingleList() {
        return singleList;
    }

    public void setSingleList(List<SElasticSingle> singleList) {
        this.singleList = singleList;
    }

    public List<SElasticTerm> getTerms() {
        return terms;
    }

    public void setTerms(List<SElasticTerm> terms) {
        this.terms = terms;
    }

    public List<SElasticRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<SElasticRange> ranges) {
        this.ranges = ranges;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<SElasticSort> getSorts() {
        return sorts;
    }

    public void setSorts(List<SElasticSort> sorts) {
        this.sorts = sorts;
    }
}

