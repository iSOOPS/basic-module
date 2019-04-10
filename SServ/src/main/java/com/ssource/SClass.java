package com.ssource;


import org.apache.commons.lang.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean isNotBlank(Object...args){
        return !isBlank(args);
    }

    public static boolean isBlank(Object...args){
        for (Object temp:args){
            if (temp instanceof String){
                if (StringUtils.isBlank((String) temp)){
                    return true;
                }
            }
            else {
                if (temp == null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取时间戳
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
     * 获取某一天0点时间戳
     * @param timeMill 时间戳
     * @param offset 偏移量 0-今天0点，-1昨天0点
     */
    public static long timeMillZerois(long timeMill,int offset){
        long timeZero = timeMill - (timeMill % (24 * 60 * 60)) - 8 * 60 * 60;
        return timeZero + offset * (24 * 60 * 60);
    }

    /**
     * 获取某个月开始第一天0点的偏移量时间戳
     */
    public static long timeMillMonthZerois(long timeMill,int offset){
        return timeMillMonthZerois(timeMill,offset,true);
    }

    /**
     * 获取某月某天的月份偏移时间戳
     */
    public static long timeMillMonthOffset(long timeMill,int offset){
        return timeMillMonthZerois(timeMill,offset,false);
    }

    private static long timeMillMonthZerois(long timeMill,int offset,boolean zero){
        Calendar ca = Calendar.getInstance();
        Date date = new Date(timeMill * 1000);
        ca.setTime(date);
        ca.add(Calendar.MONTH, offset);
        if (zero)ca.set(Calendar.DAY_OF_MONTH, 1);
        if (zero)ca.set(Calendar.HOUR_OF_DAY, 0);
        if (zero)ca.set(Calendar.MINUTE, 0);
        if (zero)ca.set(Calendar.SECOND, 0);
        return ca.getTimeInMillis() / 1000;
    }
    /**
     * 获取某年开始第一天0点的偏移量时间戳
     */
    public static long timeMillYearZerois(long timeMill,int offset) {
        return timeMillYearZerois(timeMill,offset,true);
    }

    /**
     * 获取某天的年偏移量
     */
    public static long timeMillYearOffset(long timeMill,int offset) {
        return timeMillYearZerois(timeMill,offset,true);
    }

    private static long timeMillYearZerois(long timeMill,int offset,boolean zero) {
        Calendar ca = Calendar.getInstance();
        Date date = new Date(timeMill * 1000);
        ca.setTime(date);
        ca.add(Calendar.YEAR, offset);
        if (zero)ca.set(Calendar.DAY_OF_MONTH, 1);
        if (zero)ca.set(Calendar.HOUR_OF_DAY, 0);
        if (zero)ca.set(Calendar.MINUTE, 0);
        if (zero)ca.set(Calendar.SECOND, 0);
        return ca.getTimeInMillis() / 1000;
    }

    /**
     * 时间戳转换格式
     * @param timeMill 时间戳
     */
    public static String timeMillisToDate(long timeMill){
        return timeMillisToDate(timeMill,"yyyy-MM-dd HH:mm:ss");
    }

    public static String timeMillisToDate(long timeMill,String format){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(timeMill * 1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间转换为时间戳
     * @param string 时间date
     */
    public static long dateToTimeMillis(String string){
        return dateToTimeMillis(string,"yyyy-MM-dd HH:mm:ss");
    }

    public static long dateToTimeMillis(String string,String format){
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(string);
        } catch (ParseException e) {

        }
        if (date == null){
            return 0;
        }
        return date.getTime() / 1000;
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
     * 判断string是否为数字
     * @param string 字符串
     * @return f
     */
    public static boolean isNumberic(String string){
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(string);
        if( !isNum.matches() ){
            return false;
        }
        return true;
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
