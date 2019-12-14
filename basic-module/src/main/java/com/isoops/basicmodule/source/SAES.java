package com.isoops.basicmodule.source;

import com.isoops.basicmodule.source.aes.*;
import com.isoops.basicmodule.source.aes.exception.AESConfigParamsError;
import com.isoops.basicmodule.source.aes.exception.CipherCreateError;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

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
}
