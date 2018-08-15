package com.elasticsearch;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import java.util.Map;

/**
 * Created by Samuel on 2017/1/17.
 */
public class SETest {

    private Client client;

    public void init(){
//        //设置集群名字
//        Settings settings = Settings.settingsBuilder()
//                .put("cluster.name", SEEConf.es_public_cluster_name)
//                .put("client.transport.sniff", true)
//                . build();
//
//        client = new TransportClient.Builder().settings(settings).build()
//                .addTransportAddress(
//                        new InetSocketTransportAddress(
//                                new InetSocketAddress(SEEConf.es_public_note_one,port)))
//                .addTransportAddress(
//                        new InetSocketTransportAddress(
//                                new InetSocketAddress(SEEConf.es_public_note_two,port)));
    }

    public Client getClient(){
        return client;
    }

    /**
     * 增加 索引／类型／id／数据
     * @param bean 数据对象
     */
    public void addDocument(SElasticBase bean) {
        if (!bean.checkNullPrivate() && bean.getJsonMap()!=null){
            return;
        }
        IndexResponse response = client
                .prepareIndex(bean.index, bean.type, bean.id)
                .setSource(bean.getJsonMap())
                .get();
        System.out.printf(String.valueOf(response));

    }
    /**
     * 根据 索引／类型／id 获取一条数据
     * @param bean 数据对象
     * @return
     */
    public Map<String, Object> getDocument(SElasticBase bean) {
        if (!bean.checkNullPrivate() ){
            return null;
        }
        try {
            GetResponse response = client.prepareGet(bean.index, bean.type, bean.id).get();
            Map<String, Object> map = response.getSource();
            return map;

        }catch (Exception e){
            return null;
        }

    }

}
