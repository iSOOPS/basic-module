package com;


import com.redissouce.SRedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainTest {
    public static void main(String[] args) {
//        SHttp.getInstance().download(
//                "http://img1.3lian.com/img013/v4/96/d/41.jpg",
//                "/Users/samuel/Documents/Flies/aaa.jpg",
//                new SHttp.HttpClientDownLoadProgress() {
//
//                    @Override
//                    public void onProgress(int progress) {
//                        System.out.println("download progress = " + progress);
//                    }
//                });
//        SHttp.getInstance().download(
//                "http://img1.3lian.com/img013/v4/96/d/41.jpg",
//                "/Users/samuel/Documents/Flies/aaa.jpg");
//        System.out.println("download end");
//
//        SHttp.getInstance().downloadInputStream("http://img1.3lian.com/img013/v4/96/d/41.jpg");
//        GLOBALSINGLETON.S().REDIS_DEVELOP_HOST = "193.112.127.253";
//        GLOBALSINGLETON.S().REDIS_TEST_HOST = "193.112.127.253";
//        GLOBALSINGLETON.S().REDIS_PUBLIC_HOST = "127.0.0.1";
//        GLOBALSINGLETON.S().REDIS_PORT = 6379;
//        //侦测jvm环境，并缓存到全局变量中
//        String env = System.getProperty("spring.profiles.active");
//        if(env!=null && env.equals("development")) {
//            GLOBALSINGLETON.S().ENVIRONMENT = GLOBALSINGLETON.ENVIRONMENTENUM.DEVELOP;
//        }
//        else if (env!=null && env.equals("test")){
//            GLOBALSINGLETON.S().ENVIRONMENT = GLOBALSINGLETON.ENVIRONMENTENUM.TEST;
//        }
//        else {
//            GLOBALSINGLETON.S().ENVIRONMENT = GLOBALSINGLETON.ENVIRONMENTENUM.RELASE;
//        }
//        for (int i=0;i<5;i++){
//            SRedis.s().getStringFromRedis("WECHAT_TOKEN_CACHE_111111");
//        }

//        Integer total = 100;
//        Integer size = 2;
//        List<Integer> list = new ArrayList<>();
//        for (int i=size;i>0;i--){
//            Integer n = 0;
//            if (i == 1) {
//                n = total;
//            }
//            else {
//                Random r     = new Random();
//                int min   = 1; //
//                int max   = total / i * 2;
//                n = r.nextInt(max);
//                n = n <= min ? 1: n;
//            }
//            total = total - n;
//            list.add(n);
//        }

    }


}
