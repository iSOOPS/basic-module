package com.isoops.basicmodule.classes.annotation;

import com.isoops.basicmodule.classes.annotation.source.BasicContract;
import com.isoops.basicmodule.classes.interceptor.SException;
import com.isoops.basicmodule.classes.basicmodel.GenericEnum;
import com.isoops.basicmodule.classes.basicmodel.Request;
import com.isoops.basicmodule.classes.interceptor.SResponseUriCache;
import com.isoops.basicmodule.classes.annotation.source.SignGenerater;
import com.isoops.basicmodule.common.redis.SRedis;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class CheckRequestContract extends BasicContract {

    @Autowired
    private SignGenerater signGenerater;

    @Autowired
    private SRedis sRedis;

    @Pointcut("@annotation(CheckRequest)")
    private void permission() {

    }

    @Before("permission()")
    public void doBefore() {

    }

    @After("permission()")
    public void doAfter() {

    }

    /**
     * 环绕
     * 会将目标方法封装起来
     * 具体验证业务数据
     */
    @Around("permission()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        CheckRequest checkRequest = method.getAnnotation(CheckRequest.class);


        Request<?> bean = checkRequest.isUrl() ? GetArgsWithUrl(request) : GetArgsModel(proceedingJoinPoint);
        checkAction(request,bean,checkRequest);

        String rui = request.getRequestURI();
        SResponseUriCache.getInstance().set(rui, true);
        // 执行具体方法
        return proceedingJoinPoint.proceed();
    }

    private void checkAction(HttpServletRequest request,
                                Request<?> bean,
                                CheckRequest checkRequest) throws Throwable{
        if (bean == null){
            throw new SException(GenericEnum.FORMAT_ERROR);
        }
        switch (checkRequest.level()) {
            case NO_CHECK:{
                break;
            }
            case LOCK:{
                String ip = GetIpAddr(request);
                String uri = request.getRequestURI();
                boolean status = ipLock(ip,uri,checkRequest.seconds(),checkRequest.maxCount());
                if (!status){
                    log.info("用户IP[" + ip + "]接口[" + uri + "]超过了限定的次数[" + checkRequest.maxCount() + "]");
                    throw new SException(GenericEnum.REPETITION_ERROR);
                }
                break;
            }
            case SIGN_CHECK: {
                if (!signGenerater.macthSign(bean.getUserSignal(),bean.getSign())){
                    throw new SException(GenericEnum.SIGN_ERROR);
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean ipLock(String ip,String uri,Long time,Long maxCount){
        String key = "REQ_LIMINT_".concat(uri).concat(ip);
        long count = sRedis.incr(key, 1L);
        if (count == 1) {
            sRedis.expire(key, time, TimeUnit.SECONDS);
        }
        if (count > maxCount) {
            return false;
        }
        return true;
    }
}
