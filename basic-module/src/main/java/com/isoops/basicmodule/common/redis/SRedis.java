package com.isoops.basicmodule.common.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.isoops.basicmodule.common.redis.source.RedisLock;
import com.isoops.basicmodule.common.redis.source.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SRedis {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisLock redisLock;

    public void set(String key, Object obj) {
        if (key == null || key.equals("") || obj == null){
            return;
        }
        redisUtil.set(key,(obj instanceof String) ? (String) obj : JSON.toJSONString(obj));
    }

    public void set(String key, Object obj ,Integer seconds) {
        if (key == null || key.equals("") || obj == null || seconds == null){
            return;
        }
        redisUtil.setEx(key,(obj instanceof String) ? (String) obj : JSON.toJSONString(obj),seconds,TimeUnit.SECONDS);
    }

    public void set(String key, Object obj ,Long time,TimeUnit unit) {
        if (key == null || key.equals("") || obj == null){
            return;
        }
        if (time == null || unit == null){
            set(key,obj);
            return;
        }
        redisUtil.setEx(key,(obj instanceof String) ? (String) obj : JSON.toJSONString(obj),time,unit);
    }



    public String get(String key){
        if (key == null || key.equals("")){
            return null;
        }
        return redisUtil.get(key);
    }

    public <T>T get(String key, Class<T> tClass){
        if (key == null || key.equals("") || tClass == String.class){
            return null;
        }
        String value = redisUtil.get(key);
        if (value == null){
            return null;
        }
        return JSON.parseObject(value,tClass);
    }

    public <T>List<T> getList(String key, Class<T> tClass){
        if (key == null || key.equals("")){
            return null;
        }
        String value = redisUtil.get(key);
        return JSONArray.parseArray(value,tClass);
    }

    public void delete(String key) {
        redisUtil.delete(key);
    }

    public void delete(String ...arg) {
        List<String> list = Arrays.asList(arg);
        redisUtil.delete(list);
    }

    /**
     * 增加(自增长), 负数则为自减
     */
    public Long incr(String key,Long count){
        return redisUtil.incrBy(key,count);
    }

    public boolean expire(String key,Long time,TimeUnit unit){
        return redisUtil.expire(key,time,unit);
    }


    /**
     * 检查key是否存在
     */
    public boolean checkKey(String key) {
        return redisUtil.hasKey(key);
    }

    private static String LOCKKEYREDIS = "LOCKKEYREDIS_";

    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁
     * 锁key-手动UUID
     */
    public boolean lockKeyWithUUID(String key,String uuid,Integer seconds){
        set(LOCKKEYREDIS+key,uuid,seconds);
        return redisLock.lock(key,uuid,seconds);
    }

    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁 /当key不存在当时候才会成功
     * 锁key-自动UUID
     */
    public boolean lockKey(String key,Integer seconds){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号
        uuid = uuid.substring(0,8)+uuid.substring(9,13)+uuid.substring(14,18)+uuid.substring(19,23)+uuid.substring(24);
        return lockKeyWithUUID(key,uuid,seconds);
    }

    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁
     * 解锁key
     */
    public boolean unLockKey(String key){
        String uuid = get(LOCKKEYREDIS+key,String.class);
        if (uuid!=null){
            return redisLock.unLock(key);
        }
        return false;
    }

}
