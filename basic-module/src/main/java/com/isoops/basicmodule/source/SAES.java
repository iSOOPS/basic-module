package com.isoops.basicmodule.source;

import com.isoops.basicmodule.source.aes.*;
import com.isoops.basicmodule.source.aes.exception.AESConfigParamsError;
import com.isoops.basicmodule.source.aes.exception.CipherCreateError;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

/**
 * Created by Samuel on 16/9/9.
 */
@Component
public class SAES {

    //默认为 CBC PKCS5padding 128位
    public String encrypt(String key,String value){
        if (SClass.isBlank(key,value)){
            return null;
        }
        AESUtil aesUtil = getAESUtil(key,AESLevel.DEFAULT,AESOperationMode.DEFAULT,AESPaddingMode.DEFAULT);
        try {
            return aesUtil == null ? null : aesUtil.decryptBase64String(value);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String uncrypt(String key,String value){
        if (SClass.isBlank(key,value)){
            return null;
        }
        AESUtil aesUtil = getAESUtil(key,AESLevel.DEFAULT,AESOperationMode.DEFAULT,AESPaddingMode.DEFAULT);
        try {
            return aesUtil == null ? null : aesUtil.decryptBase64String(value);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }



    public String encrypt(String key,String value,AESLevel level,AESOperationMode mode,AESPaddingMode padding){
        if (SClass.isBlank(key,value)){
            return null;
        }
        AESUtil aesUtil = getAESUtil(key,level,mode,padding);
        try {
            return aesUtil == null ? null : aesUtil.decryptBase64String(value);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String uncrypt(String key,String value,AESLevel level,AESOperationMode mode,AESPaddingMode padding){
        if (SClass.isBlank(key,value)){
            return null;
        }
        AESUtil aesUtil = getAESUtil(key,level,mode,padding);
        try {
            return aesUtil == null ? null : aesUtil.decryptBase64String(value);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AESUtil getAESUtil(String key,AESLevel level,AESOperationMode mode,AESPaddingMode padding){
        AESConfig aesConfig = new AESConfig();
        aesConfig.setKey(key);
        aesConfig.setLevel(level);
        aesConfig.setOperationMode(mode);
        aesConfig.setPaddingMode(padding);
        try {
            return new AESUtil(aesConfig);
        } catch (AESConfigParamsError | InvalidAlgorithmParameterException | InvalidKeyException | CipherCreateError aesConfigParamsError) {
            aesConfigParamsError.printStackTrace();
        }
        return null;
    }

    public static String unCode7AES(String data,String key,String iv){
        String idDecrypt = null;
        try {
            byte[] keyByte = new BASE64Decoder().decodeBuffer(key);
            byte[] dataByte = new BASE64Decoder().decodeBuffer(data);
            byte[] ivByte = new BASE64Decoder().decodeBuffer(iv);

            idDecrypt = decrypt7(dataByte,keyByte,ivByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (idDecrypt!=null){
            return idDecrypt;
        }
        return null;
    }

    public static String decrypt7(byte[] dataByte, byte[] keyByte, byte[] ivByte){
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            // 初始化
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return result;
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | UnsupportedEncodingException | NoSuchProviderException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
