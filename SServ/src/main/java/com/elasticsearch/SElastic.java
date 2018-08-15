package com.elasticsearch;


import com.GLOBALSINGLETON;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Samuel on 16/8/1.
 */
public class SElastic {



    static Logger logger = LogManager.getLogger(SElastic.class.getName());

    private  static int port = GLOBALSINGLETON.S().ES_PORT;

    private TransportClient client;

    private volatile static SElastic singleton;

    private SElastic (){}

    public static SElastic getSingleton() {
        if (singleton == null) {
            synchronized (SElastic.class) {
                if (singleton == null) {
                    singleton = new SElastic();

                    try {
                        if (GLOBALSINGLETON.S().ENVIRONMENT == GLOBALSINGLETON.ENVIRONMENTENUM.RELASE){
                            Settings settings = Settings.builder()
                                    .put("client.transport.sniff", true)
                                    .put("cluster.name", GLOBALSINGLETON.S().ES_CLUSTER_NAME).build();
                            InetAddress addr1 = InetAddress.getByName(GLOBALSINGLETON.S().ES_NOTE_MASTER);
                            InetAddress addr2 = InetAddress.getByName(GLOBALSINGLETON.S().ES_NOTE_SLAVES.get(0));
                            InetAddress addr3 = InetAddress.getByName(GLOBALSINGLETON.S().ES_NOTE_SLAVES.get(1));
                            singleton.client = new PreBuiltTransportClient(settings)
                                    .addTransportAddress(new InetSocketTransportAddress(addr1, port))
                                    .addTransportAddress(new InetSocketTransportAddress(addr2, port))
                                    .addTransportAddress(new InetSocketTransportAddress(addr3, port));
                        }
                        else {
                            Settings settings = Settings.builder()
                                    .put("client.transport.sniff", false)
                                    .put("cluster.name", GLOBALSINGLETON.S().ES_CLUSTER_NAME).build();
                            String host = (GLOBALSINGLETON.S().ENVIRONMENT == GLOBALSINGLETON.ENVIRONMENTENUM.TEST)?GLOBALSINGLETON.S().ES_NOTE_MASTER_TEST:GLOBALSINGLETON.S().ES_NOTE_MASTER_DEVELOP;
                            InetAddress addr = InetAddress.getByName(host);
                            singleton.client = new PreBuiltTransportClient(settings)
                                    .addTransportAddress(new InetSocketTransportAddress(addr, port));
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return singleton;
    }

    /**
     * 根据别名判断索引是否存在
     * @param index 索引
     * @return f
     */
    public boolean isExists(String index) {
        String checkIndex = index.replace("#","").toLowerCase();
        IndicesExistsResponse existsResponse = this.client.admin().indices().prepareExists(checkIndex).get();
        return existsResponse.isExists();
    }
    /**
     * 判断指定的索引的类型是否存在
     * @param index 索引名
     * @param type 索引类型
     * @return  f
     */
    public boolean isExistsType(String index,String type){
        String checkIndex = index.replace("#","").toLowerCase();
        TypesExistsResponse response = this.client.admin().indices().typesExists(new TypesExistsRequest(new String[]{checkIndex}, type)).actionGet();
        return response.isExists();
    }
    /**
     * 根据索引别名 删除索引
     * @param index 索引
     * @return f
     */
    public boolean deleteIndex(String index) {
        String checkIndex = index.replace("#","").toLowerCase();
        try {
            DeleteIndexResponse response = this.client.admin().indices().
                    delete(new DeleteIndexRequest(checkIndex)).actionGet();
            return response.isAcknowledged();
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 为索引添加一个别名
     * @param index index 索引
     * @param alias 别名
     * @return f
     */
    public boolean addAliasForIndex(String index, String alias) {
        String checkIndex = index.replace("#","").toLowerCase();
        String checkAlias = alias.replace("#","").toLowerCase();
        IndicesAliasesResponse response = this.client.admin().indices()
                .prepareAliases().addAlias(checkIndex, checkAlias).execute().actionGet();
        return response.isAcknowledged();
    }

    /**
     * 创建索引
     * @param index 索引名称
     * @param type 类型
     * @param mapping xml上下文
     */
    public void creatIndex(String index,String type,XContentBuilder mapping){
        try {
            String checkIndex = index.replace("#","").toLowerCase();

            PutMappingRequest putmap = Requests.putMappingRequest(checkIndex).type(type).source(mapping);
            //创建索引
            this.client.admin().indices().prepareCreate(checkIndex).execute().actionGet();
            //为索引添加映射
            this.client.admin().indices().putMapping(putmap).actionGet();
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建索引类型
     * @param index 索引
     * @param type 类型
     * @param mapping xml上下文
     */
    public void creatType(String index, String type,XContentBuilder mapping) {
        try {
            String checkIndex = index.replace("#","").toLowerCase();

            this.client.admin().indices().preparePutMapping(checkIndex)
                    .setType(type).setSource(mapping)
                    .execute().actionGet();
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加 索引／类型／id／数据
     * @param bean 数据对象
     */
    public SEResultObject addDocument(SElasticBase bean) {
        SEResultObject object = new SEResultObject(false);
        if (!bean.checkNullPrivate() && bean.getJsonMap()!=null){
            object.setMsg("数据对象不能为空");
            return object;
        }
        try {
            IndexRequestBuilder indexRequestBuilder = this.client
                    .prepareIndex(bean.index, bean.type, bean.id)
                    .setSource(bean.getJsonMap(), XContentType.JSON);
            IndexResponse response = indexRequestBuilder.get();
            object.changeState(true);
            return object;
        }catch (Exception e){
            logger.error("Elasticsearch addDocument error:"+e);
            logger.error("Elasticsearch addDocument bean:"+JSON.toJSONString(bean));
            object.setMsg("搜索服务又开小差了");
            object.setStateCode(401);
            return object;
        }
    }

    /**
     * 批量增加 索引／类型／id／数据
     * @param beanList 数据对象数组
     */
    public SEResultObject addDocuments(List<SElasticBase> beanList) {
        SEResultObject object = new SEResultObject(false);
        BulkRequestBuilder bulkRequest = this.client.prepareBulk();
        for (SElasticBase bean:beanList){
            if (!bean.checkNullPrivate() || bean.getJsonMap()==null){
                object.setMsg("数据对象不能为空");
                return object;
            }
            bulkRequest.add(this.client.prepareIndex(bean.index, bean.type, StringUtils.trim(bean.id)).setSource(bean.getJsonMap()));
        }
        try {
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()){
                return object;
            }
            object.changeState(true);
            return object;
        }catch (Exception e){
            logger.warn("Elasticsearch addDocuments error:"+e);
            logger.warn("Elasticsearch addDocument bean:"+JSON.toJSONString(beanList));
            object.setMsg("搜索服务又开小差了");
            object.setStateCode(401);
            return object;
        }
    }

    /**
     * 根据 索引／类型／id 删除一条数据
     * @param bean 数据对象
     */
    public SEResultObject delDocument(SElasticBase bean) {
        SEResultObject object = new SEResultObject(false);
        if (!bean.checkNullPrivate() || bean.id==null ||bean.type==null ||bean.index==null){
            object.setMsg("数据对象不能为空");
            return object;
        }
        try {
            DeleteResponse response = this.client.prepareDelete(bean.index,bean.type,bean.id).get();
            object.changeState(true);
            return object;
        }catch (Exception e){
            logger.warn("Elasticsearch delDocument error:"+e);
            logger.warn("Elasticsearch delDocument bean:"+JSON.toJSONString(bean));
            object.setMsg("搜索服务又开小差了");
            object.setStateCode(401);
            return object;
        }
    }

    /**
     * 根据  索引／类型／id 更新一条数据 里的 某个字段
     * （如果需要更新大量字段 建议使用addDocument方法）
     * @param bean
     */
    public SEResultObject updateDocument(SElasticBase bean) {
        SEResultObject object = new SEResultObject(false);
        if (!bean.checkNullPrivate()||
                bean.id==null ||
                bean.terms==null||
                bean.terms.size()<1 ||
                bean.terms.get(0).key==null ||
                bean.terms.get(0).value==null){
            object.setMsg("数据对象不能为空");
            return object;
        }
        try {
            UpdateRequest updateRequest = new UpdateRequest(bean.index, bean.type, bean.id);
            updateRequest.doc(XContentFactory
                    .jsonBuilder()
                    .startObject()
                    .field(bean.terms.get(0).key, bean.terms.get(0).value)
                    .endObject());
            this.client.update(updateRequest).get();
            object.changeState(true);
            return object;
        }catch (Exception e){
            logger.warn("updateDocument error:"+e);
            logger.warn("updateDocument bean:"+JSON.toJSONString(bean));

            object.setMsg("搜索服务又开小差了");
            object.setStateCode(401);
            return object;
        }
    }

    /**
     * 根据 索引／类型／id 获取一条数据
     * @param bean 数据对象
     * @return
     */
    public SEResultObject getDocument(SElasticBase bean) {
        SEResultObject object = new SEResultObject(false);
        if (!bean.checkNullPrivate() ){
            object.setMsg("数据对象不能为空");
            return object;
        }
        try {
            GetResponse response = this.client.prepareGet(bean.index, bean.type, bean.id).get();
            Map<String, Object> map = response.getSource();
            object.setObject(map);
            object.changeState(true);
            return object;
        }catch (Exception e){
            logger.warn("Elasticsearch getDocument error:"+e);
            logger.warn("Elasticsearch getDocument bean:"+JSON.toJSONString(bean));
            object.setMsg("搜索服务又开小差了");
            object.setStateCode(401);
            return object;
        }
    }

    /**
     * 根据 索引／类型／value条件／key-value条件／范围条件 模糊检索数据（使用ik中文分词）
     * @param bean
     */
    public SEResultObject getDocuments_ik(SElasticBase bean) {
        SEResultObject object = new SEResultObject(false);
        if (bean.index==null || bean.type==null){
            object.setMsg("数据对象不能为空");
            return object;
        }
        BoolQueryBuilder bq = QueryBuilders.boolQuery();
        if (bean.terms != null && bean.terms.size()>0) {
            for (SElasticTerm elasticTerm : bean.terms) {
                switch (elasticTerm.type){
                    case must:{
                        if (elasticTerm.isMulti == true){
                            bq = bq.must(QueryBuilders.multiMatchQuery(elasticTerm.value,elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase==true){
                            bq = bq.must(QueryBuilders.matchPhraseQuery(elasticTerm.key, elasticTerm.value));
                        }else {
                            bq = bq.must(QueryBuilders.matchQuery(elasticTerm.key, elasticTerm.value));
                        }
                        break;
                    }
                    case should:{
                        if (elasticTerm.isMulti == true){
                            bq = bq.should(QueryBuilders.multiMatchQuery(elasticTerm.value,elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase==true){
                            bq = bq.should(QueryBuilders.matchPhraseQuery(elasticTerm.key, elasticTerm.value));
                        }else {
                            bq = bq.should(QueryBuilders.matchQuery(elasticTerm.key, elasticTerm.value));
                        }
                        break;
                    }
                    case mustNot:{
                        if (elasticTerm.isMulti == true){
                            bq = bq.mustNot(QueryBuilders.multiMatchQuery(elasticTerm.value, elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase==true){
                            bq = bq.mustNot(QueryBuilders.matchPhraseQuery(elasticTerm.key, elasticTerm.value));
                        }else {
                            bq = bq.mustNot(QueryBuilders.matchQuery(elasticTerm.key, elasticTerm.value));
                        }
                        break;
                    }
                    default:{
                        if (elasticTerm.isMulti == true){
                            bq = bq.must(QueryBuilders.multiMatchQuery(elasticTerm.value, elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase==true){
                            bq = bq.must(QueryBuilders.matchPhraseQuery(elasticTerm.key, elasticTerm.value));
                        }else {
                            bq = bq.must(QueryBuilders.matchQuery(elasticTerm.key, elasticTerm.value));
                        }
                        break;
                    }
                }
            }
        }
        //构造 全文单string 查询参数
        if (bean.singleList!=null && bean.singleList.size()>0){
            for (SElasticSingle elasticSingle : bean.singleList) {
                switch (elasticSingle.type){
                    case must:{
                        bq = bq.must(QueryBuilders.queryStringQuery(elasticSingle.value));
                        break;
                    }
                    case should:{
                        bq = bq.should(QueryBuilders.queryStringQuery(elasticSingle.value));
                        break;
                    }
                    case mustNot:{
                        bq = bq.mustNot(QueryBuilders.queryStringQuery(elasticSingle.value));
                        break;
                    }
                    default:{
                        bq = bq.must(QueryBuilders.queryStringQuery(elasticSingle.value));
                        break;
                    }
                }
            }
        }
        //构造 范围 查询参数
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (bean.ranges != null && bean.ranges.size() > 0) {
            for (SElasticRange elasticRange : bean.ranges) {
                if (elasticRange.key!=null && elasticRange.from!=null && elasticRange.to!=null) {
                    switch (elasticRange.type){
                        case must:{
                            qb = qb.must(QueryBuilders
                                    .rangeQuery(StringUtils.trim(elasticRange.key))//查询字段
                                    .from(StringUtils.trim(elasticRange.from))//开始边界
                                    .to(StringUtils.trim(elasticRange.to))//结束边界
                                    .includeLower(true)     //包括下界
                                    .includeUpper(true)); //包括上界
                            break;
                        }
                        case should:{
                            qb = qb.should(QueryBuilders
                                    .rangeQuery(StringUtils.trim(elasticRange.key))
                                    .from(StringUtils.trim(elasticRange.from))
                                    .to(StringUtils.trim(elasticRange.to))
                                    .includeLower(true)
                                    .includeUpper(true));
                            break;
                        }
                        case mustNot:{
                            qb = qb.mustNot(QueryBuilders
                                    .rangeQuery(StringUtils.trim(elasticRange.key))
                                    .from(StringUtils.trim(elasticRange.from))
                                    .to(StringUtils.trim(elasticRange.to))
                                    .includeLower(true)
                                    .includeUpper(true));
                            break;
                        }
                        default:{
                            qb = qb.must(QueryBuilders
                                    .rangeQuery(StringUtils.trim(elasticRange.key))
                                    .from(StringUtils.trim(elasticRange.from))
                                    .to(StringUtils.trim(elasticRange.to))
                                    .includeLower(true)
                                    .includeUpper(true));
                            break;
                        }
                    }
                }
            }
        }
        //构造排序参数
        SortBuilder sortBuilder = null;
        if (bean.sorts != null && bean.sorts.size()>0) {
            for (SElasticSort elasticSort : bean.sorts) {
                if (elasticSort.key==null){
                    return null;
                }
                sortBuilder = SortBuilders
//                        .fieldSort(elasticSort.key+".keyword")
                        .fieldSort(elasticSort.key)
                        .order(elasticSort.isASC_DESC==true?SortOrder.ASC : SortOrder.DESC);
            }
        }
        //构造 查询
        SearchRequestBuilder searchRequestBuilder = this.client
                .prepareSearch(bean.index)//索引
                .setTypes(bean.type)//类型
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)//搜索方式 精确搜索（SCAN 无需扫描搜索）
                .setFrom(bean.pageIndex)//分页 下标
                .setSize(bean.pageSize)//分页 分页大小
                .setExplain(true);//返回搜索响应信息
        if (bq != null)searchRequestBuilder .setQuery(bq); //搜索条件
        if (qb != null)searchRequestBuilder .setPostFilter(qb); //范围 搜索条件
        if (sortBuilder != null)searchRequestBuilder .addSort(sortBuilder);//排序条件

        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        SearchResponse response = null;
        //查询
        try {
            response = searchRequestBuilder.execute().actionGet();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (response == null){
            long i = 0;
            object.setDataCount(i);
            object.setObject(lists);
            object.changeState(true);
            return object;
        }
        //取值
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            lists.add(hit.getSource());
        }
        object.setDataCount(hits.getTotalHits());
        object.setObject(lists);

        object.changeState(true);
        return object;
    }
}
