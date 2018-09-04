package com.ssource;


import org.apache.commons.lang.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Samuel on 16/7/6.
 */
public class SClass{

    /**
     * 计算总页数
     * @return
     */
    public static Integer getPageSizeCount(Integer allCount,Integer pageSize){
        if (allCount==null || pageSize == null){
            return 0;
        }
        float i = allCount%pageSize;
        if (i ==0){
            return allCount/pageSize;
        }
        return allCount/pageSize+1;
    }


    /**
     * 获取时间戳
     * @return
     */
    public static String timeMillis(){
        return String.valueOf(timeMillisLong());
    }
    public static Long timeMillisLong(){
        return System.currentTimeMillis()/1000;
    }

    /**
     * 获取今天0点时间戳
     * offset 偏移量 0-今天0点，-1昨天0点
     * @return f
     */
    public static long timeMillZerois(int offset){
        Date date = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        Date date2 = new Date(date.getTime() - gc.get(gc.HOUR_OF_DAY) * 60 * 60
                * 1000 - gc.get(gc.MINUTE) * 60 * 1000 - gc.get(gc.SECOND)
                * 1000);
        return date2.getTime()/1000 + (offset * 24*60*60);
    }

    /**
     * 获取当月开始时间戳 第一天0点
     * @return 时间戳
     */
    public static long timeMillMonthStartTime(String timeMill){
        Calendar c = Calendar.getInstance();
        if (timeMill!=null)c.setTime(new Date(Long.valueOf(timeMill)*1000));
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
    /**
     * 获取当月结束时间戳 最后一天0点
     * @return 时间戳
     */
    public static long timeMillMonthEndTime(String timeMill) {
        Calendar ca = Calendar.getInstance();
        if (timeMill!=null)ca.setTime(new Date(Long.valueOf(timeMill)));
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND,0);
        ca.set(Calendar.MILLISECOND, 0);
        return ca.getTimeInMillis();
    }

    public static String timeMillisToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt*1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 打印
     * @param object
     */
    public static void log(Object object){
        System.out.println(object);
    }
    /**
     * 将一个 Map 对象转化为一个 JavaBean
     * @param type 要转化的类型
     * @param map 包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException
     *             如果分析类属性失败
     * @throws IllegalAccessException
     *             如果实例化 JavaBean 失败
     * @throws InstantiationException
     *             如果实例化 JavaBean 失败
     * @throws InvocationTargetException
     *             如果调用属性的 setter 方法失败
     */
    public static Object convertMap(Class type, Map map)
            throws IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象

        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                args[0] = value;

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }

    /**
     * 获取一个特定长度的随机数
     * @param length 长度
     * @return f
     */
    public static String randomString(Integer length) {
        Integer countLength = length;
        StringBuffer numStr = new StringBuffer();
        int num;
        for (int i = 0; i < countLength; i++) {
            // Math.random() 随机出0-1之间的实数，返回值是一个double 类型的
            num = (int) (Math.random() * 10);
            numStr.append(String.valueOf(num));
        }
        return numStr.toString();
    }

    /**
     * 获取一个随机数数组(数字均不一致)
     * @param size 数组数量
     * @param start 开始数字
     * @param end 结束数字
     * @return f
     */
    public static List<Integer> randomList(int size,int start,int end) {
        //1.创建集合容器对象
        List list = new ArrayList();
        //2.创建Random对象
        Random r = new Random();
        while(list.size() != size){
            int num = r.nextInt(end-start) + start;
            if(!list.contains(num)){
                list.add(num);
            }
        }
        return list;
    }

    /**
     * 获取一个随机数数组(固定数组数据总和)
     * @param total 数组数据总和
     * @param size 数组长度
     * @return f
     */
    public static List<Integer> randomList(int total, int size) {
        List<Integer> list = new ArrayList<>();
        for (int i=size;i>0;i--){
            Integer n;
            if (i == 1) {
                n = total;
            }
            else {
                Random r     = new Random();
                int min   = 1; //
                int max   = total / i * 2;
                n = r.nextInt(max);
                n = n <= min ? 1: n;
            }
            total = total - n;
            list.add(n);
        }
        return list;
    }

    /**
     * 过滤特殊字符
     */
    public static String filterEmoji(String source) {
        if(StringUtils.isNotBlank(source)){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
        }else{
            return source;
        }
    }
}
