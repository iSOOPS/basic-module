package com.isoops.basicmodule.classes.annotation;


import com.isoops.basicmodule.classes.annotation.source.BasicContract;
import com.isoops.basicmodule.classes.annotation.source.InterceptorException;
import com.isoops.basicmodule.common.redis.SRedis;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class ControllerLockContract extends BasicContract {

    @Autowired
    private SRedis sRedis;

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(req)")
    public void controllerLock(final JoinPoint joinPoint, ControllerLock req) throws InterceptorException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = GetIpAddr(request);
        String interfaceName = request.getRequestURI();
        String key = "REQ_LIMINT".concat(interfaceName).concat(ip);
        long count = sRedis.incr(key, 1L);
        if (count == 1) {
            sRedis.expire(key, req.seconds(), TimeUnit.SECONDS);
        }
        if (count > req.maxCount()) {
            log.info("用户IP[" + ip + "]接口[" + interfaceName + "]超过了限定的次数[" + req.maxCount() + "]");
            throw new InterceptorException("访问过于频繁");
        }
    }


}
