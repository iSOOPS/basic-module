package com.isoops.basicmodule.common.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class SRedis {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private boolean blank(Object...args){
        for (Object temp:args){
            if (temp == null || temp==""){
                return true;
            }
        }
        return false;
    }

    public boolean set(String key, Object obj) {
        return set(key,obj,null,null);
    }

    public boolean set(String key, Object obj ,Long seconds) {
        return set(key,obj, seconds,TimeUnit.SECONDS);
    }

    public boolean set(String key, Object obj ,Long time,TimeUnit unit) {
        if (blank(key,obj,time,unit)){
            return false;
        }
        try {
            if (time == null || unit == null){
                redisTemplate.opsForValue().set(key, (obj instanceof String) ? (String) obj : JSON.toJSONString(obj));
            }
            else {
                redisTemplate.opsForValue().set(key, (obj instanceof String) ? (String) obj : JSON.toJSONString(obj), time, unit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String get(String key){
        if (blank(key)){
            return null;
        }
        return redisTemplate.opsForValue().get(key);
    }

    public <T>T get(String key, Class<T> tClass){
        if (blank(key) || tClass == String.class){
            return null;
        }
        String value = redisTemplate.opsForValue().get(key);
        if (value == null){
            return null;
        }
        return JSON.parseObject(value,tClass);
    }
    public <T>List<T> get(Collection<String> keys, Class<T> tClass) {
        if (blank(keys) || keys.size()<1){
            return null;
        }
        List<String> strings = redisTemplate.opsForValue().multiGet(keys);
        List<T> response = new ArrayList<>();
        if (strings != null) {
            for (String json : strings){
                response.add(JSON.parseObject(json,tClass));
            }
        }
        return response;
    }

    public <T>List<T> getList(String key, Class<T> tClass){
        if (blank(key)){
            return null;
        }
        String value = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(value,tClass);
    }

    public boolean delete(String key) {
        if (blank(key)){
            return false;
        }
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String ...arg) {
        List<String> list = Arrays.asList(arg);
        if (list.size()<1){
            return false;
        }
        try {
            redisTemplate.delete(list);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 增加(自增长), 负数则为自减
     */
    public Long incr(String key,Long increment){
        if (blank(key,increment)){
            return null;
        }
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key,Long time,TimeUnit unit){
        if (blank(key,time,unit)){
            return false;
        }
        try {
            return redisTemplate.expire(key,time,unit);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     */
    public Boolean persist(String key) {
        if (blank(key)){
            return false;
        }
        return redisTemplate.persist(key);
    }

    /**
     * 返回 key 的剩余的过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        if (blank(key,unit)){
            return null;
        }
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 检查key是否存在
     */
    public boolean hasKey(String key) {
        if (blank(key)){
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    /**
     * 查找匹配的key
     */
    public Set<String> getKeys(String pattern) {
        if (blank(pattern)){
            return null;
        }
        return redisTemplate.keys(pattern);
    }



    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁 /当key不存在当时候才会成功
     */
    public boolean setnx(String key ,String value ,Long time,TimeUnit unit){
        if (blank(key,value,time,unit)){
            return false;
        }
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, unit);
    }

    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁
     * 解锁key
     */
    public boolean deletenx(String key){
        if (blank(key)){
            return false;
        }
        return redisTemplate.opsForValue().getOperations().delete(key);
    }

}
