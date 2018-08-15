package com.ssource;
import java.security.MessageDigest;

/**
 * Created by Samuel on 16/7/11.
 */
public class SMD5 {
    /**
     * 采用MD5加密解密
     * @author tfq
     * @datetime 2011-10-13
     */
    /***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }
    // 测试主函数
    public static void main1(String a) {
        String s = new String(a);
        System.out.println("enter：        " + s);
        System.out.println("MD5->：        " + string2MD5(s));
        System.out.println("uncode1：      " + convertMD5(s));
        System.out.println("uncode2：      " + convertMD5(convertMD5(s)));

    }

    public static String enCode2MD5(String a) {
        String newStr = new String(a);
        return string2MD5(newStr);
    }
    public static String enCodeConvert(String a) {
        String newStr = new String(a);
        return convertMD5(newStr);
    }


}
