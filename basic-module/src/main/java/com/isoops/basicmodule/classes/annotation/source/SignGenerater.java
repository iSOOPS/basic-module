package com.isoops.basicmodule.classes.annotation.source;

import com.isoops.basicmodule.source.SClass;
import com.isoops.basicmodule.common.redis.SRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SignGenerater {

    @Autowired
    private SRedis redis;

    private static final String SIGNRECORD = "SIGNRECORD_";

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
    public String signCreater(String userSignal, Long validityTime, TimeUnit unit){
        String ramdomString = SClass.random(6,SClass.RAMDOMTYPE.LETTER_ALL,true);
        String signBasic = userSignal + ramdomString;
        String sign = bcryptEncode(signBasic);
        boolean statue = redis.set(SIGNRECORD + userSignal,sign,validityTime,unit);
        return statue ? sign : null;
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
        if (!redis.hasKey(SIGNRECORD + userSignal)){
            return false;
        }
        String getSign = redis.get(SIGNRECORD + userSignal);
        if (getSign == null || getSign.equals("")){
            return false;
        }
        return getSign.equals(sign);
    }

    public boolean cleanSign(String userSignal){
        redis.delete(userSignal);
        return true;
    }
}
