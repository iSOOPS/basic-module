package com.elasticsearch;


import com.GLOBALSINGLETON;
import com.alibaba.fastjson.JSON;
import com.ssource.SBean;
import com.ssource.SClass;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Samuel on 16/8/1.
 */
public class SElastic {

    static Logger logger = LogManager.getLogger(SElastic.class.getName());

    private static String ES_TYPE = "isoops";

    private static int port = GLOBALSINGLETON.S().ES_PORT;

    private TransportClient client;

    private volatile static SElastic singleton;

    private Map<String,XContentBuilder> builder;

    public Map<String, XContentBuilder> getBuilder() {
        return builder == null ? new HashMap<String, XContentBuilder>() : builder;
    }

    public void setBuilderContent(String key,XContentBuilder content) {
        if (key == null || content == null){
            return;
        }
        if (builder == null){
            builder = new HashMap<>();
        }
        builder.put(key,content);
    }

    private SElastic (){}

    public static SElastic s() {
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
    public synchronized boolean isExists(String index) {
        String checkIndex = index.replace("#","").toLowerCase();
        IndicesExistsResponse existsResponse = this.client.admin().indices().prepareExists(checkIndex).get();
        return existsResponse.isExists();
    }

    /**
     * 根据索引别名 删除索引
     * @param index 索引
     * @return f
     */
    public synchronized boolean deleteIndex(String index) {
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
    public synchronized boolean addAliasForIndex(String index, String alias) {
        String checkIndex = index.replace("#","").toLowerCase();
        String checkAlias = alias.replace("#","").toLowerCase();
        IndicesAliasesResponse response = this.client.admin().indices()
                .prepareAliases().addAlias(checkIndex, checkAlias).execute().actionGet();
        return response.isAcknowledged();
    }

    /**
     * 创建索引
     * @param index 索引名称
     * @param builderKey 索引接口key
     */
    public synchronized SEResultObject creatIndex(String index,String builderKey){
        XContentBuilder content = getBuilder().get(builderKey);
        if (content == null){
            return new SEResultObject("索引结构不存在");
        }
        try {
            String checkIndex = index.replace("#","").toLowerCase();
            PutMappingRequest putmap = Requests.putMappingRequest(checkIndex).type(ES_TYPE).source(content);
            //创建索引
            this.client.admin().indices().prepareCreate(checkIndex).execute().actionGet();
            //为索引添加映射
            this.client.admin().indices().putMapping(putmap).actionGet();
            return new SEResultObject(true);
        } catch (ElasticsearchException e) {
            logger.error("Elasticsearch creatIndex error:"+e);
            logger.error("Elasticsearch creatIndex index:"+index);
            logger.error("Elasticsearch creatIndex builderKey:"+builderKey);
            logger.error("Elasticsearch creatIndex builder:"+JSON.toJSONString(getBuilder()));
            return new SEResultObject("搜索服务又开小差了");
        }
    }



    /**
     * 增加 索引／类型／id／数据
     * @param alias 别名
     * @param key id
     * @param obj 对象
     * @param <T> 对象类型
     * @return f
     */
    public synchronized <T>SEResultObject set(String alias,String key,T obj) {
        if (alias == null || key == null || obj == null){
            return new SEResultObject("数据对象不能为空");
        }
        try {
            IndexRequestBuilder indexRequestBuilder = this.client
                    .prepareIndex(alias, ES_TYPE, key)
                    .setSource(JSON.toJSONString(obj), XContentType.JSON);
            indexRequestBuilder.get();
            return new SEResultObject(true);
        }catch (Exception e){
            logger.error("Elasticsearch set error:"+e);
            logger.error("Elasticsearch set alias:"+alias);
            logger.error("Elasticsearch set key:"+alias);
            logger.error("Elasticsearch set obj:"+JSON.toJSONString(obj));
            return new SEResultObject("搜索服务又开小差了");
        }
    }

    /**
     * 批量增加 索引／类型／id／数据
     * @param alias 别名
     * @param list 数组对象
     * @param <T> 对象类型
     * @return
     */
    public synchronized <T>SEResultObject set(String alias,List<SElasticSet<T>> list) {
        if (alias == null || list == null){
            return new SEResultObject("数据对象不能为空");
        }
        BulkRequestBuilder bulkRequest = this.client.prepareBulk();
        for (SElasticSet set:list){
            if (!set.checkData()){
                return new SEResultObject("数据对象不能为空");
            }
            bulkRequest.add(this.client.prepareIndex(alias, ES_TYPE, StringUtils.trim(set.getKey())).setSource(JSON.toJSONString(set.getObject())));
        }
        try {
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()){
                return new SEResultObject(false);
            }
            return new SEResultObject(true);
        }catch (Exception e){
            logger.error("Elasticsearch sets error:"+e);
            logger.error("Elasticsearch sets alias:"+alias);
            logger.error("Elasticsearch sets alias:"+JSON.toJSONString(list));
            return new SEResultObject("搜索服务又开小差了");
        }
    }

    /**
     * 根据 索引／类型／id 删除一条数据
     * @param alias 别名
     * @param key id
     * @return f
     */
    public synchronized SEResultObject delete(String alias,String key) {
        if (alias == null || key == null){
            return new SEResultObject("数据对象不能为空");
        }
        try {
            this.client.prepareDelete(alias,ES_TYPE,key).get();
            return new SEResultObject(true);
        }catch (Exception e){
            logger.error("Elasticsearch delete error:"+e);
            logger.error("Elasticsearch set alias:"+alias);
            logger.error("Elasticsearch set key:"+alias);
            return new SEResultObject("搜索服务又开小差了");
        }
    }

    /**
     * 根据 索引／类型／id 获取一条数据
     * @param alias 别名
     * @param key id
     * @return f
     */
    public <T> SEResultObject get(String alias,String key,Class<T> t) {
        if (alias == null || key == null){
            return new SEResultObject("数据对象不能为空");
        }
        try {
            GetResponse response = this.client.prepareGet(alias,ES_TYPE,key).get();
            Map<String, Object> map = response.getSource();
            T object = SBean.mapToBean(map,t);
            return new SEResultObject(object);
        }catch (Exception e){
            logger.warn("Elasticsearch get error:"+e);
            logger.warn("Elasticsearch get alias:"+alias);
            logger.warn("Elasticsearch get key:"+key);
            logger.warn("Elasticsearch get Class:"+t);
            return new SEResultObject("搜索服务又开小差了");
        }
    }

    /**
     * 根据 索引／类型／value条件／key-value条件／范围条件 模糊检索数据（使用ik中文分词）
     */
    public <T> SEResultObject<T> get(String alias,
                                     List<SElasticTerm> terms,
                                     List<SElasticSingle> singles,
                                     List<SElasticRange> ranges,
                                     List<SElasticSort> sorts,
                                     Integer pageIndex,
                                     Integer pageSize,
                                     Class<T> t) {
        if (alias == null){
            return new SEResultObject<>("数据对象不能为空");
        }
        BoolQueryBuilder bq = QueryBuilders.boolQuery();
        if (terms != null && terms.size()>0) {
            for (SElasticTerm elasticTerm : terms) {
                switch (elasticTerm.type){
                    case must:{
                        if (elasticTerm.isMulti){
                            bq = bq.must(QueryBuilders.multiMatchQuery(elasticTerm.value,elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase){
                            bq = bq.must(QueryBuilders.matchPhraseQuery(elasticTerm.key, elasticTerm.value));
                        }else {
                            bq = bq.must(QueryBuilders.matchQuery(elasticTerm.key, elasticTerm.value));
                        }
                        break;
                    }
                    case should:{
                        if (elasticTerm.isMulti){
                            bq = bq.should(QueryBuilders.multiMatchQuery(elasticTerm.value,elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase){
                            bq = bq.should(QueryBuilders.matchPhraseQuery(elasticTerm.key, elasticTerm.value));
                        }else {
                            bq = bq.should(QueryBuilders.matchQuery(elasticTerm.key, elasticTerm.value));
                        }
                        break;
                    }
                    case mustNot:{
                        if (elasticTerm.isMulti){
                            bq = bq.mustNot(QueryBuilders.multiMatchQuery(elasticTerm.value, elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase){
                            bq = bq.mustNot(QueryBuilders.matchPhraseQuery(elasticTerm.key, elasticTerm.value));
                        }else {
                            bq = bq.mustNot(QueryBuilders.matchQuery(elasticTerm.key, elasticTerm.value));
                        }
                        break;
                    }
                    default:{
                        if (elasticTerm.isMulti){
                            bq = bq.must(QueryBuilders.multiMatchQuery(elasticTerm.value, elasticTerm.keys));
                        }
                        else if (elasticTerm.isPhrase){
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
        if (singles!=null && singles.size()>0){
            for (SElasticSingle elasticSingle : singles) {
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
        if (ranges != null && ranges.size() > 0) {
            for (SElasticRange elasticRange : ranges) {
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
        if (sorts != null && sorts.size()>0) {
            for (SElasticSort elasticSort : sorts) {
                if (elasticSort.key==null){
                    return null;
                }
                sortBuilder = SortBuilders
//                        .fieldSort(elasticSort.key+".keyword")
                        .fieldSort(elasticSort.key)
                        .order(elasticSort.isASC_DESC ? SortOrder.ASC : SortOrder.DESC);
            }
        }
        //构造 查询
        SearchRequestBuilder searchRequestBuilder = this.client
                .prepareSearch(alias)//索引
                .setTypes(ES_TYPE)//类型
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)//搜索方式 精确搜索（SCAN 无需扫描搜索）
                .setFrom(pageIndex)//分页 下标
                .setSize(pageSize)//分页 分页大小
                .setExplain(true);//返回搜索响应信息
        if (bq != null)searchRequestBuilder .setQuery(bq); //搜索条件
        if (qb != null)searchRequestBuilder .setPostFilter(qb); //范围 搜索条件
        if (sortBuilder != null)searchRequestBuilder .addSort(sortBuilder);//排序条件

        List<T> lists = new ArrayList<>();
        SearchResponse response = null;
        //查询
        try {
            response = searchRequestBuilder.execute().actionGet();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (response == null){
            long i = 0;
            return new SEResultObject(i,lists);
        }
        //取值
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            lists.add(SBean.mapToBean(hit.getSource(),t));
        }
        return new SEResultObject(hits.getTotalHits(),lists);
    }

    public  <T> SEResultObject reindexEngineer(String alias, List<SElasticSet<T>> list, String builderKey) {
        if (alias == null || list == null){
            return new SEResultObject("数据对象不能为空");
        }
        //编辑新索引别名
        String newIndex = alias + SClass.timeMillis();
        //新建索引类型
        SEResultObject resultObject = creatIndex(newIndex, builderKey);
        if (!resultObject.getState()){
            return resultObject;
        }
        //重建索引
        resultObject = set(alias,list);
        if (resultObject.getState()){
            //通过索引别名删除旧索引
            deleteIndex(alias);
            //绑定别名到新索引
            Boolean statusBind = addAliasForIndex(newIndex,alias);
            if (!statusBind){
                deleteIndex(newIndex);
                return new SEResultObject("重建索引失败");
            }
        }
        else {
            //回滚操作，删除创建索引
            deleteIndex(newIndex);
        }
        return resultObject;
    }

}
