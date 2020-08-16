package com.isoops.basicmodule.classes.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存类
 * 缓存需要统一封装的接口，避免每次请求都需要反射
 *
 * @author ljy
 * @since 2020/3/30 5:31 下午
 */
public class SResponseUriCache {
    /**
     * 缓存map
     */
    private final Map<String, Boolean> cacheMap = new HashMap<>();

    /**
     * 禁止指令重排优化
     */
    private static volatile SResponseUriCache URI_CACHE;

    private SResponseUriCache(){}

    public static SResponseUriCache getInstance(){
        if(null == URI_CACHE){
            synchronized(SResponseUriCache.class){
                if(null == URI_CACHE){
                    URI_CACHE = new SResponseUriCache();
                }
            }
        }
        return URI_CACHE;
    }

    /**
     *  设置缓存值
     * @param key key
     * @param value value
     */
    public void set(String key, Boolean value) {
        cacheMap.put(key, value);
    }

    /**
     *  获取缓存key的value值
     * @param key key
     * @return boolean
     */
    public Boolean get(String key) {
        if (!cacheMap.containsKey(key)) {
            return false;
        }
        return cacheMap.get(key);
    }
}
