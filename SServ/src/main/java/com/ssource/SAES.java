package com.ssource;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Samuel on 16/9/9.
 */
public class SAES {

    // 加密
    public static String encrypt(String sSrc, String encodingFormat, String sKey, String ivParameter) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(encodingFormat));
        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
    }

    // 解密
    public static String decrypt(String sSrc, String encodingFormat, String sKey, String ivParameter) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original,encodingFormat);
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }


    public static String enCodeAES(String data,String key,String iv){
        String idEncrypt = null;
        if (key == null || iv == null || iv.length()!=16){
            return idEncrypt;
        }
        try {
            idEncrypt = encrypt(data,"utf-8",key,iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (idEncrypt!=null){
            return idEncrypt;
        }
        return null;
    }
    public static String unCodeAES(String data,String key,String iv){
        String idDecrypt = null;
        if (key == null || iv == null || iv.length()!=16){
            return idDecrypt;
        }
        try {
            idDecrypt = decrypt(data,"utf-8",key,iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (idDecrypt!=null){
            return idDecrypt;
        }
        return null;
    }
}
