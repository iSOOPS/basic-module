package com.isoops.basicmodule.source;



import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

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

    private static final Pattern numberPattern = Pattern.compile("-?[0-9]+.?[0-9]+");

    public static Integer getPageSizeCount(Integer allCount,Integer pageSize){
        if (allCount==null || pageSize == null){
            return 0;
        }
        float i = allCount % pageSize;
        if (i ==0){
            return allCount / pageSize;
        }
        return allCount / pageSize+1;
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
            else if (temp instanceof List){
                if (((List) temp).size() < 1){
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

    public static class RAMDOMTYPE {
        final public static String NUMBER_ONLY = "0123456789";
        final public static String LETTER_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        final public static String LETTER_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final public static String LETTER_ALL = LETTER_LOWERCASE + LETTER_UPPERCASE;
        final public static String SYMBOL = "/:_!@#$%^&";
        final public static String ALL = NUMBER_ONLY + SYMBOL + LETTER_ALL;

    }

    public static String random(Integer length,String source,Boolean canSame) {
        Random r = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        Integer size = 0;
        while (!size.equals(length)){
            int index = r.nextInt(source.length()-1);
            if (canSame || stringBuffer.toString().indexOf(source.charAt(index)) == -1){
                stringBuffer.append(source.charAt(index));
                size ++ ;
            }
        }
        return stringBuffer.toString();
    }

    public static List<?> randomList(Integer size,String source,Boolean canSame){
        return Collections.singletonList(random(size, source,canSame));
    }

    public static List<Integer> splitNumberToList(Integer number,Integer size){
        List<Integer> list = new ArrayList<>();
        for (int i=size;i>0;i--){
            Integer n;
            if (i == 1) {
                n = number;
            }
            else {
                Random r     = new Random();
                int min   = 1; //
                int max   = number / i * 2;
                n = r.nextInt(max);
                n = n <= min ? 1: n;
            }
            number = number - n;
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
        Matcher isNum = numberPattern.matcher(string);
        return isNum.matches();
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
