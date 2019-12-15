package com.isoops.basicmodule.source;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
/**
 * Created by samuel on 2017/6/22.
 */
public class SBean {


    public static void transMap2HiberanteBean(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                //将key中大写字符转为_大写
                key = underscoreName(key);

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }

        } catch (Exception e) {
            System.out.println("transMap2Bean Error " + e);
        }

        return;
    }

    /**
     * <p class="detail">
     * 功能：将大写转为下划线,且小写 如: userName > user_name
     * </p>
     * @author wuxw
     * @param name
     * @return
     * @throws
     */
    private static String underscoreName(String name)
    {
        StringBuilder result = new StringBuilder();
        if ((name != null) && (name.length() > 0)) {
            result.append(name.substring(0, 1).toLowerCase());
            for (int i = 1; i < name.length(); ++i) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase())) {
                    result.append("_");
                    result.append(s.toLowerCase());
                }
                else {
                    result.append(s);
                }
            }
        }
        return result.toString();
    }



    public static <T> T mapToBean(Map map, Class<T> t) {
        if (map == null || t == null) {
            return null;
        }
        try {
            T bean = t.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> mapsToBeans(List<Map> list, Class<T> t) {
        if (list == null || t == null) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (Map map : list) {
            T object = mapToBean(map, t);
            if (object != null) {
                result.add(object);
            }
        }
        return result;
    }



    public static Map beanToMap(Object obj) {
        if (obj == null) {
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

    public static List<Map> beansToMaps(List<Object> list) {
        if (list == null || list.size() < 1) {
            return null;
        }
        List<Map> maps = new ArrayList<>();
        for (Object object : list) {
            Map map = beanToMap(object);
            if (map == null) {
                continue;
            }
            maps.add(map);
        }
        return maps;
    }


    public static <T> T beanToBean(Object bean, Class<T> t) {
        if (bean == null) {
            return null;
        }
        T newBean;
        try {
            newBean = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        BeanUtils.copyProperties(bean,newBean);
        return newBean;
    }


    public static <T> List<T> beansToBeans(List list, Class<T> t) {
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

    public static <T> T mixBeans(Class<T> t, Object... args) {
        T newBean;
        try {
            newBean = t.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        for (Object temp : args) {
            try {
                PropertyUtils.copyProperties(newBean, temp);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
        return newBean;
    }

    /**
     * 合并两个数组对象结构
     *
     * @param basicList        合并对象
     * @param subList          被合并对象
     * @param basicCheckKey    校验相同的key对应的value == true 则合并
     * @param subCheckKey      校验相同的key对应的value == true 则合并
     * @param condistionString 被合并对象成为合并对象里的字段名
     * @return 合并后数组-以basicList为主体
     */
    public static List<Map> mixListToListByCondistion(List basicList,
                                                      List subList,
                                                      String basicCheckKey,
                                                      String subCheckKey,
                                                      String condistionString) {
        List<Map> resultList = new ArrayList<>();
        for (Object basicObj : basicList) {
            Map<String, Object> map = (basicObj instanceof Map) ? (Map<String, Object>) basicObj : SBean.beanToMap(basicObj);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.putAll(map);
            for (Object subObj : subList) {
                Object value;
                if (subObj instanceof String) {
                    value = subObj;
                } else {
                    Map<String, Object> mapSub = (subObj instanceof Map) ? (Map<String, Object>) subObj : SBean.beanToMap(subObj);
                    value = mapSub.get(subCheckKey);
                }
                if (value != null && map.get(basicCheckKey) != null && value.equals(map.get(basicCheckKey))) {
                    resultMap.put(condistionString, subObj);
                }
            }
            resultList.add(resultMap);
        }
        return resultList;
    }

    /**
     * 同上，但是排序是根据subList排序
     */
    public static List<Map> mixListToListByCondistionDesc(List basicList,
                                                          List subList,
                                                          String basicCheckKey,
                                                          String subCheckKey,
                                                          String condistionString) {
        List<Map> resultList = new ArrayList<>();
        for (Object subObj : subList) {
            Map<String, Object> resultMap = new HashMap<>();
            Object value;
            Map<String, Object> mapSub = (subObj instanceof Map) ? (Map<String, Object>) subObj : SBean.beanToMap(subObj);
            if (subObj instanceof String) {
                value = subObj;
            } else {
                value = mapSub.get(subCheckKey);
            }
            for (Object basicObj : basicList) {
                Map<String, Object> map = (basicObj instanceof Map) ? (Map<String, Object>) basicObj : SBean.beanToMap(basicObj);
                if (value != null && map.get(basicCheckKey) != null && value.equals(map.get(basicCheckKey))) {
                    resultMap.putAll(map);
                    resultMap.put(condistionString, subObj);
                    break;
                }
            }
            resultList.add(resultMap);
        }
        return resultList;
    }

    public static <T> List<T> getListValueList(List basicList, String key) {
        List<T> resultList = new ArrayList<>();
        if (basicList == null) return resultList;
        for (Object obj : basicList) {
            if (obj instanceof String) {
                return resultList;
            } else if (obj instanceof Integer) {
                return resultList;
            } else if (obj instanceof Long) {
                return resultList;
            } else {
                Map<String, Object> map = SBean.beanToMap(obj);
                T getObj = (T) map.get(key);
                if (getObj != null) {
                    resultList.add(getObj);
                }
            }
        }
        return resultList;
    }

    /**
     * 求数组所有对象某个字段的和/求数字数组的的和
     *
     * @param list      数组
     * @param objectKey 对象的key
     * @return f
     */
    public static Long mixListNumbers(List list, String objectKey) {
        if (list == null) return null;
        Long number = Long.valueOf(0);
        for (Object obj : list) {
            if (objectKey == null) {
                number = number + sumNumber(number, obj);
            } else {
                Map<String, Object> map = SBean.beanToMap(obj);
                Object getObj = map.get(objectKey);
                if (getObj == null) {
                    continue;
                }
                number = number + sumNumber(number, getObj);
            }
        }
        return number;
    }

    /**
     * 删除数组元素
     *
     * @param list         数组
     * @param index        删除下标
     * @param includeLower 是否删除包含下标
     * @param direction    删除左偏移元素、删除右偏移元素
     * @return f
     */
    public static boolean removeListItemByIndexRange(List list,
                                                     Integer index,
                                                     boolean includeLower,
                                                     boolean direction) {
        if (list == null || list.size() < index) {
            return false;
        }
        if (direction) {
            for (int i = 0; i < index + (includeLower ? 1 : 0); i++) {
                list.remove(0);
            }
        } else {
            for (int i = 0; i < list.size() - index + 1 + (includeLower ? 1 : 0); i++) {
                list.remove(list.size() - 1);
            }
        }
        return true;
    }

    public static Long sumNumber(Long number, Object sumNumber) {
        String strNumber = String.valueOf(sumNumber);
        if (isNumberic(strNumber)) {
            number = number + Long.valueOf(strNumber);
        }
        return number;
    }

    private static boolean isNumberic(String string) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(string);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
