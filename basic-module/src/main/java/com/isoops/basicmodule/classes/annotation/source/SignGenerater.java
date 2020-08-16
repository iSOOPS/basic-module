package com.isoops.basicmodule.classes.annotation.source;

import com.isoops.basicmodule.source.SMD5;
import com.isoops.basicmodule.source.SUUIDGenerater;
import com.isoops.basicmodule.common.redis.SRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Component
public class SignGenerater {

    @Autowired
    private SRedis sRedis;

    /**
     * 创建一个sign（使用redis存储并且启动非redis分布式的锁/默认2个小时失效）
     * @param code code
     * @param validityTime 有效时间-值
     * @param unit 有效时间单位
     * @return f
     */
    public String signCreater(String code, Long validityTime, TimeUnit unit){
        if (code == null || code == ""){
            return null;
        }
        validityTime = validityTime == null ? 2 : validityTime;
        unit = unit == null ? TimeUnit.HOURS : unit;
        String uuid = SUUIDGenerater.getCleanUUID();
        sRedis.lockKeyWithUUID(code,uuid, 15);
        sRedis.set(code,uuid,validityTime,unit);
        return uuid;
    }

    /**
     * 创建一个code
     * @param request 请求头.replace(".","")
     * @param userSignal 用户的标示
     * @return f
     */
    public String codeCreater(HttpServletRequest request, String userSignal){
        String ip = request.getRemoteAddr().replace(".","").replace(":","");
        String codeBasic = userSignal+ip;
        return SMD5.enCode2MD5(codeBasic);
    }

    public boolean checkCode(String code, HttpServletRequest request, String userSignal){
        return codeCreater(request, userSignal).equals(code);
    }

    /**
     * 校验sign是否正确
     * @param code code
     * @param sign sign
     * @return f
     */
    public boolean checkSign(String code,String sign){
        if (!sRedis.checkKey(code)){
            return false;
        }
        return sign.equals(sRedis.get(code));
    }

    public boolean cleanSign(String code){
        sRedis.delete(code);
        return true;
    }
}
