package com;

import java.util.List;

public class GLOBALSINGLETON {

    public enum ENVIRONMENTENUM {
        RELASE , //发布环境
        TEST ,   //测试环境
        DEVELOP  //开发环境
    }
    public ENVIRONMENTENUM ENVIRONMENT;

    public String ES_NOTE_MASTER_DEVELOP;
    public String ES_NOTE_MASTER_TEST;
    public String ES_NOTE_MASTER;
    public List<String> ES_NOTE_SLAVES;

    public String ES_CLUSTER_NAME;
    public Integer ES_PORT;

    public String REDIS_DEVELOP_HOST;
    public String REDIS_TEST_HOST;
    public String REDIS_PUBLIC_HOST;

    public Integer REDIS_PORT;

    //单例
    private static GLOBALSINGLETON globalsingleton;

    private GLOBALSINGLETON(){

    }

    public static GLOBALSINGLETON S(){
        if(globalsingleton == null){
            globalsingleton = new GLOBALSINGLETON();
        }
        return globalsingleton;
    }



}
