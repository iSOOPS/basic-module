package com.isoops.basicmodule.classes.annotation.source;

import com.alibaba.fastjson.JSON;
import com.isoops.basicmodule.source.SClass;
import com.isoops.basicmodule.source.SMD5;
import com.isoops.basicmodule.source.SUUIDGenerater;
import com.isoops.basicmodule.common.redis.SRedis;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Component
public class SignGenerater {

    @Autowired
    private SRedis redis;

    private static String SIGNRECORD = "SIGNRECORD_";

    public String bcryptEncode(String key){
        BCryptPasswordEncoder encoder =new BCryptPasswordEncoder();
        return encoder.encode(key);
    }

    public boolean match(String key,String source){
        BCryptPasswordEncoder encoder =new BCryptPasswordEncoder();
        return encoder.matches(key,source);
    }

    /**
     * 创建一个code
     * @param userSignal 用户的标示
     * @return f
     */
    public String codeCreater(String userSignal, Long validityTime, TimeUnit unit){
        String ramdomString = SClass.random(6,SClass.RAMDOMTYPE.LETTER_ALL,true);
        String codeBasic = userSignal + ramdomString;
        String md5Code = SMD5.enCode2MD5(codeBasic);
        boolean statue = redis.set(SIGNRECORD + userSignal,md5Code,validityTime,unit);
        return statue ? md5Code : null;
    }

    public boolean macthRule(String userSignal ,String sign, Object obj){
        if (userSignal == null || userSignal.equals("") ||
                sign == null || sign.equals("") ||
                obj == null){
            return false;
        }
        String objectJson = JSON.toJSONString(obj);
        return match(userSignal + objectJson,sign);
    }

    /**
     * 校验sign是否正确
     * @return f
     */
    public boolean macthSign(String userSignal ,String sign){
        if (userSignal == null || userSignal.equals("") ||
                sign == null || sign.equals("")){
            return false;
        }
        if (!redis.hasKey(userSignal)){
            return false;
        }
        String code = redis.get(SIGNRECORD + userSignal);
        if (code == null || code.equals("")){
            return false;
        }
        return match(userSignal + code,sign);
    }

    public boolean macthSignHighLevel(String userSignal , String sign, Object obj){
        if (userSignal == null || userSignal.equals("") ||
                sign == null || sign.equals("") ||
                obj == null){
            return false;
        }
        if (!redis.hasKey(userSignal)){
            return false;
        }
        String code = redis.get(SIGNRECORD + userSignal);
        if (code == null || code.equals("")){
            return false;
        }
        String objectJson = JSON.toJSONString(obj);
        return match(userSignal + code + objectJson,sign);
    }

    public boolean cleanSign(String userSignal){
        redis.delete(userSignal);
        return true;
    }
}
