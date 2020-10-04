package com.isoops.basicmodule.source;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;

/**
 * Created by samuel on 2017/6/22.
 */
public class SBean {

    private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();
    private static final Pattern linePattern = Pattern.compile("_(\\w)");
    private static final Pattern humpPattern = Pattern.compile("[A-Z]");
    private static final Pattern numberPattern = Pattern.compile("-?[0-9]+.?[0-9]+");

    /**
     * 字段下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 字段驼峰转下划线
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static<T> T mapToBean(Map<String,Object> map, Class<T> t) {
        if (map == null || t == null) {
            return null;
        }
        T bean = null;
        try {
            bean = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    public static <T> List<T> mapsToBeans(List<Map<String,Object>> list, Class<T> t) {
        if (list == null || t == null) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (Map<String,Object> map : list) {
            T object = mapToBean(map, t);
            if (object != null) {
                result.add(object);
            }
        }

        return result;
    }

    public static<T> Map<String,Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key+"", beanMap.get(key));
            }
        }
        return map;
    }

    public static<T> List<Map<String,Object>> beansToMaps(List<T> list) {
        if (list == null || list.size() < 1) {
            return null;
        }
        List<Map<String,Object>> maps = new ArrayList<>();
        for (Object object : list) {
            Map<String,Object> map = beanToMap(object);
            maps.add(map);
        }
        return maps;
    }


    public static<T> T beanToBean(Object bean, Class<T> t) {
        if (bean == null) {
            return null;
        }
        T newBean;
        try {
            newBean = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        copy(bean,newBean);
        return newBean;
    }


    public static <T> List<T> beansToBeans(List<?> list, Class<T> t) {
        List<T> resultList = new ArrayList<>();
        if (list == null || t == null) {
            return resultList;
        }
        for (Object object : list) {
            T get = beanToBean(object, t);
            if (get != null) {
                resultList.add(get);
            }
        }
        return resultList;
    }

    /**
     * 合并多个对象成一个bean
     */
    public static <T> T mixBeans(Class<T> t, Object... args) {
        T newBean;
        try {
            newBean = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        for (Object temp : args) {
            copy(temp,newBean);
        }
        return newBean;
    }

    private static void copy(Object source, Object target) {
        String key = source.getClass().getName() + target.getClass().getName();
        BeanCopier beanCopier;
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            beanCopier = BEAN_COPIER_CACHE.get(key);
        } else {
            beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            BEAN_COPIER_CACHE.put(key, beanCopier);
        }
        beanCopier.copy(source, target, null);
    }

    /**
     * 替换map中null成空字符串
     */
    public static Map<String,Object> replaceNullToSomething(Map<String,Object> map,Object something){
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String,Object> entry:entries){
            if (entry.getValue() == null){
                entry.setValue(something);
            }
        }
        return map;
    }

    /**
     * 获取数组里对象的某个字段，重新组成数组
     */
    public static<T,V> List<V> getValueToList(List<T> basicList, SFunction<T,V> key) {
        List<V> resultList = new ArrayList<>();
        if (basicList == null) {
            return resultList;
        }
        for (T o : basicList) {
            V val = key.apply(o);
            resultList.add(val);
        }
        return resultList;
    }

    /**
     * 获取数组里对象的某个字段，重新组成数组
     */
    public static<T,V> void setValueToList(List<T> basicList, SFunction<T,V> key , V object) {
        if (basicList == null) {
            return;
        }
        for (T o : basicList) {
            String keyName = getLambdaName(key);
            try {
                BeanUtils.setProperty(o,keyName,object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> String getLambdaName(SFunction<T, ?> fn) {
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);

        // 从lambda信息取出method、field、class等
        String fieldName = serializedLambda.getImplMethodName().substring("get".length());
        fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
        Field field;
        try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        // 从field取出字段名，可以根据实际情况调整
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && tableField.value().length() > 0) {
            return tableField.value();
        } else {
            return fieldName.replaceAll("[A-Z]", "_$0").toLowerCase();
        }
    }

    /**
     * 求数组所有对象某个字段的和/求数字数组的的和
     */
    public static<T> Long mixListNumbers(List<T> list, String objectKey) {
        if (list == null) {
            return null;
        }
        Long number = 0L;
        for (T obj : list) {
            String strNumber = String.valueOf(obj);
            Matcher isNum = numberPattern.matcher(strNumber);

            if (objectKey == null) {
                number = number + (isNum.matches() ? Long.parseLong(strNumber) : 0);
            } else {
                Map<String, Object> map = SBean.beanToMap(obj);
                Object getObj = map.get(objectKey);
                if (getObj == null) {
                    continue;
                }
                number = number + (isNum.matches() ? Long.parseLong(strNumber) : 0);
            }
        }
        return number;
    }

    /**
     * 删除数组元素
     * @param list         数组
     * @param index        删除下标
     * @param includeLower 是否删除包含下标
     * @param direction    删除左偏移元素、删除右偏移元素
     * @return f
     */
    public static<T> boolean removeListItemByIndexRange(List<T> list,
                                                        Integer index,
                                                        boolean includeLower,
                                                        boolean direction) {
        if (list == null || list.size() < index) {
            return false;
        }
        if (direction) {
            if (index + (includeLower ? 1 : 0) > 0) {
                list.subList(0, index + (includeLower ? 1 : 0)).clear();
            }
        } else {
            list.subList(index, list.size() - 1).clear();
        }
        return true;
    }

    /**
     * 笛卡尔积算法
     */
    public static<T> void descartes(List<List<T>> targetList,
                                    List<List<T>> result,
                                    int layer,
                                    List<T> curList) {
        if (layer < targetList.size() - 1) {
            if (targetList.get(layer).size() == 0) {
                descartes(targetList, result, layer + 1, curList);
            } else {
                for (int i = 0; i < targetList.get(layer).size(); i++) {
                    List<T> list = new ArrayList<>(curList);
                    list.add(targetList.get(layer).get(i));
                    descartes(targetList, result, layer + 1, list);
                }
            }
        } else if (layer == targetList.size() - 1) {
            if (targetList.get(layer).size() == 0) {
                result.add(curList);
            } else {
                for (int i = 0; i < targetList.get(layer).size(); i++) {
                    List<T> list = new ArrayList<>(curList);
                    list.add(targetList.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }
}
