package com.elasticsearch;


import com.GLOBALSINGLETON;
import com.alibaba.fastjson.JSON;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.elasticsearch.query.SElasticRange;
import com.elasticsearch.query.SElasticSingle;
import com.elasticsearch.query.SElasticSort;
import com.elasticsearch.query.SElasticTerm;
import com.elasticsearch.result.SEResultObject;
import com.elasticsearch.result.SEResultSearchObject;
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
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
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
                            singleton.client = new PreBuiltTransportClient(settings)
                                    .addTransportAddress(new TransportAddress(InetAddress.getByName(GLOBALSINGLETON.S().ES_NOTE_MASTER), port))
                                    .addTransportAddress(new TransportAddress(InetAddress.getByName(GLOBALSINGLETON.S().ES_NOTE_SLAVES.get(0)), port))
                                    .addTransportAddress(new TransportAddress(InetAddress.getByName(GLOBALSINGLETON.S().ES_NOTE_SLAVES.get(1)), port));

                        }
                        else {
                            Settings settings = Settings.builder()
                                    .put("client.transport.sniff", false)
                                    .put("cluster.name", GLOBALSINGLETON.S().ES_CLUSTER_NAME).build();
                            String host = (GLOBALSINGLETON.S().ENVIRONMENT == GLOBALSINGLETON.ENVIRONMENTENUM.TEST)?GLOBALSINGLETON.S().ES_NOTE_MASTER_TEST:GLOBALSINGLETON.S().ES_NOTE_MASTER_DEVELOP;
                            singleton.client = new PreBuiltTransportClient(settings)
                                    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
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
     * @param alias 索引
     * @return f
     */
    public synchronized boolean deleteIndex(String alias) {
        String checkAlias = alias.replace("#","").toLowerCase();
        String checkIndex = null;
        ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>>  mappingRes = client.admin().indices().prepareGetMappings(checkAlias).get().getMappings();
        ImmutableOpenMap<String, MappingMetaData> mappings = null;
        for( ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>> cursor : mappingRes){
            checkIndex = cursor.key;
        }
        if (checkIndex == null){
            return false;
        }
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
    public synchronized SEResultObject creatIndex(String index, String builderKey){
        XContentBuilder content = getBuilder().get(builderKey);
        if (content == null){
            return new SEResultObject("索引结构不存在");
        }
        index = index.replace("#","").toLowerCase();
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
        alias = alias.replace("#","").toLowerCase();
        try {
            IndexRequestBuilder indexRequestBuilder = this.client
                    .prepareIndex(alias, ES_TYPE, key)
                    .setSource(SBean.beanToMap(obj), XContentType.JSON);
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
        alias = alias.replace("#","").toLowerCase();
        BulkRequestBuilder bulkRequest = this.client.prepareBulk();
        for (SElasticSet set:list){
            if (!set.checkData()){
                return new SEResultObject("数据对象不能为空");
            }
            bulkRequest.add(this.client.prepareIndex(alias, ES_TYPE, StringUtils.trim(set.getKey())).setSource(SBean.beanToMap(set.getObject())));
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
        alias = alias.replace("#","").toLowerCase();
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
        alias = alias.replace("#","").toLowerCase();
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

    private BoolQueryBuilder queryBuilder(BoolQueryBuilder bq,QueryBuilder builder,SESEnum type){
        switch (type){
            case must:
                bq = bq.must(builder);
                break;
            case should:
                bq = bq.should(builder);
                break;
            case mustNot:
                bq = bq.mustNot(builder);
                break;
        }
        return bq;
    }
    public <T> SEResultObject<List<T>> get(String alias,
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
        alias = alias.replace("#","").toLowerCase();
        pageIndex = pageIndex == null ? 0 : pageIndex;
        pageSize = pageSize == null ? 10 : pageSize;

        BoolQueryBuilder bq = QueryBuilders.boolQuery();
        if (terms != null && terms.size()>0) {
            for (SElasticTerm term : terms) {
                QueryBuilder builder;
                if (term.keys.length > 1){
                    builder = QueryBuilders.multiMatchQuery(term.value,term.keys);
                }
                else if (term.isPhrase){
                    builder = QueryBuilders.matchPhraseQuery(term.keys[0], term.value);
                }else {
                    builder = QueryBuilders.matchQuery(term.keys[0], term.value);
                }
                queryBuilder(bq,builder,term.type);
            }
        }
        //构造 全文单string 查询参数
        if (singles!=null && singles.size()>0){
            for (SElasticSingle single : singles) {
                QueryBuilder builder = QueryBuilders.queryStringQuery(single.value);
                queryBuilder(bq,builder,single.type);
            }
        }
        //构造 范围 查询参数
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (ranges != null && ranges.size() > 0) {
            for (SElasticRange range : ranges) {
                if (range.key!=null && range.from!=null && range.to!=null) {
                    QueryBuilder builder = QueryBuilders
                            .rangeQuery(StringUtils.trim(range.key))//查询字段
                            .from(range.from)//开始边界
                            .to(range.to)//结束边界
                            .includeLower(true)     //包括下界
                            .includeUpper(true);
                    queryBuilder(bq,builder,range.type);
                }
            }
        }
        //构造排序参数
        SortBuilder sortBuilder = null;
        if (sorts != null && sorts.size()>0) {
            for (SElasticSort sort : sorts) {
                if (sort.key==null){
                    return null;
                }
                sortBuilder = SortBuilders
//                        .fieldSort(elasticSort.key+".keyword")
                        .fieldSort(sort.key)
                        .order(sort.isASC_DESC ? SortOrder.ASC : SortOrder.DESC);
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
        searchRequestBuilder.setQuery(bq); //搜索条件
        searchRequestBuilder.setPostFilter(qb); //范围 搜索条件
        if (sortBuilder != null)searchRequestBuilder .addSort(sortBuilder);//排序条件

        List<SEResultSearchObject<T>> lists = new ArrayList<>();
        SearchResponse response;
        //查询
        try {
            response = searchRequestBuilder.execute().actionGet();
        }catch (Exception e){
            return new SEResultObject<>("搜索失败");
        }
        //取值
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            T obj = SBean.mapToBean(hit.getSourceAsMap(),t);
            if (obj!=null){
                SEResultSearchObject object = new SEResultSearchObject();
                object.setIndex(hit.getIndex());
                object.setType(hit.getType());
                object.setObject(obj);
                object.setId(hit.getId());
                object.setScore(hit.getScore());
                lists.add(object);
            }
        }
        return new SEResultObject(hits.getTotalHits(),lists,hits.getMaxScore());
    }

    public  <T> SEResultObject reindexEngineer(String alias, List<SElasticSet<T>> list, String builderKey) {
        if (alias == null || list == null){
            return new SEResultObject("数据对象不能为空");
        }
        alias = alias.replace("#","").toLowerCase();
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
