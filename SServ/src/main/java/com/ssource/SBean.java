package com.ssource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel on 2017/6/22.
 */
public class SBean {

    private static Logger logger = LogManager.getLogger(SBean.class.getName());


    public static <T>T mapToBean(Map map, Class<T> t) {
        if (map == null || t == null) {
            return null;
        }
        try {
            T bean = t.newInstance();
            BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> mapsToBeans(List<Map<String, Object>> list, Class<T> t) {
        if (list == null || t == null) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (Map<String, Object> map : list){
            T object = mapToBean(map,t);
            if (object!=null){
                result.add(object);
            }
        }
        return result;
    }

    public static Map beanToMap(Object obj) {
        if(obj == null){
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    public static <T>T beanToBean(Object bean, Class<T> t){
        if (bean == null){
            return null;
        }
        T newBean;
        try {
            newBean = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        try {
            PropertyUtils.copyProperties(newBean,bean);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
        return newBean;
    }

    public static <T> List<T> beansToBeans(List list, Class<T> t){
        List<T> resultList = new ArrayList<>();
        if (list == null || t == null){
            return resultList;
        }
        for (Object object : list){
            T get = beanToBean(object,t);
            if (get!=null){
                resultList.add(get);
            }
        }
        return resultList;
    }

    public static <T>T mixBeans(Class<T> t,Object...args){
        T newBean;
        try {
            newBean = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        for (Object temp:args){
            try {
                PropertyUtils.copyProperties(newBean,temp);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
        return newBean;
    }

    /**
     * 合并两个数组对象结构
     * @param basicList 合并对象
     * @param subList 被合并对象
     * @param basicCheckKey 校验相同的key对应的value == true 则合并
     * @param subCheckKey 校验相同的key对应的value == true 则合并
     * @param condistionString 被合并对象成为合并对象里的字段名
     * @return 合并后数组-以basicList为主体
     */
    public static List<Map<String,Object>> mixListToListByCondistion(List basicList,
                                                                     List subList,
                                                                     String basicCheckKey,
                                                                     String subCheckKey,
                                                                     String condistionString){
        List<Map<String,Object>> resultList = new ArrayList<>();
        for (Object basicObj : basicList){
            Map<String,Object> map = (basicObj instanceof Map) ? (Map<String, Object>) basicObj : SBean.beanToMap(basicObj);
            Map<String , Object> resultMap = new HashMap<>();
            resultMap.putAll(map);
            for (Object subObj : subList){
                Object value;
                if (subObj instanceof String){
                    value = subObj;
                }
                else {
                    Map<String,Object> mapSub = (subObj instanceof Map) ? (Map<String, Object>) subObj : SBean.beanToMap(subObj);
                    value = mapSub.get(subCheckKey);
                }
                if (value!=null && map.get(basicCheckKey)!=null && value.equals(map.get(basicCheckKey))){
                    resultMap.put(condistionString,subObj);
                }
            }
            resultList.add(resultMap);
        }
        return resultList;
    }

    /**
     * 同上，但是排序是根据subList排序
     */
    public static List<Map<String,Object>> mixListToListByCondistionDesc(List basicList,
                                                                     List subList,
                                                                     String basicCheckKey,
                                                                     String subCheckKey,
                                                                     String condistionString){
        List<Map<String,Object>> resultList = new ArrayList<>();
        for (Object subObj : subList){
            Map<String , Object> resultMap = new HashMap<>();
            Object value;
            Map<String,Object> mapSub = (subObj instanceof Map) ? (Map<String, Object>) subObj : SBean.beanToMap(subObj);
            if (subObj instanceof String){
                value = subObj;
            }
            else {
                value = mapSub.get(subCheckKey);
            }
            for (Object basicObj : basicList){
                Map<String , Object> map = (basicObj instanceof Map) ? (Map<String, Object>) basicObj : SBean.beanToMap(basicObj);
                if (value!=null && map.get(basicCheckKey)!=null && value.equals(map.get(basicCheckKey))){
                    resultMap.putAll(map);
                    resultMap.put(condistionString,subObj);
                    break;
                }
            }
            resultList.add(resultMap);
        }
        return resultList;
    }

    public static <T> List<T> getListValueList(List basicList,String key) {
        List<T> resultList = new ArrayList<>();
        if (basicList == null) return resultList;
        for (Object obj : basicList){
            if (obj instanceof String){
                return resultList;
            }
            else if (obj instanceof Integer){
                return resultList;
            }
            else if (obj instanceof Long){
                return resultList;
            }
            else {
                Map<String,Object> map = SBean.beanToMap(obj);
                T getObj = (T) map.get(key);
                if (getObj!=null){
                    resultList.add(getObj);
                }
            }
        }
        return resultList;
    }


}
