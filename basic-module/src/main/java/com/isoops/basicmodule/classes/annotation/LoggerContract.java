package com.isoops.basicmodule.classes.annotation;

import com.alibaba.fastjson.JSON;
import com.isoops.basicmodule.classes.annotation.source.BasicContract;
import com.isoops.basicmodule.classes.annotation.source.InterceptorException;
import com.isoops.basicmodule.classes.annotation.source.LoggerEnum;
import com.isoops.basicmodule.classes.basicmodel.GenericEnum;
import com.isoops.basicmodule.classes.basicmodel.Request;
import com.isoops.basicmodule.classes.basicmodel.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

@Slf4j
@Aspect
@Component
public class LoggerContract extends BasicContract {


    @Pointcut("@annotation(Logger)")
    public void log() {}

    @Around("log() && @annotation(req)")
    public Object doAround(ProceedingJoinPoint joinPoint, final Logger req) throws Throwable {
        long startTime=System.currentTimeMillis();
        Object result=joinPoint.proceed();
        log.info("==================START RESPONSE[Desc:+"+ req.msg() +"]=================");
        if (req.logType() != LoggerEnum.ONLY_REQUEST) {
            log.info("RESPONSE:"+JSON.toJSONString(result));
        }
        log.info("TIME:"+(System.currentTimeMillis()-startTime)+"/s");
        return result;
    }

    @Before("log() && @annotation(req)")
    public void doBefore(JoinPoint joinPoint, final Logger req) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        Request bean = GetArgsModel(joinPoint);
        if (bean == null){
            try {
                throw new InterceptorException(new Response<>(GenericEnum.FORMAT_ERROR).getMsg());
            } catch (InterceptorException e) {
                e.printStackTrace();
            }
        }
        log.info("==================START REQUEST[Desc:+"+ req.msg() +"]=================");
        if (getLogBean(joinPoint).logType() != LoggerEnum.ONLY_RESPONSE){
            log.info("URL:"+request.getRequestURL().toString());
            log.info("IP:"+GetIpAddr(request));
            log.info("METHOD:"+request.getMethod());
            log.info("CLASS-METHOD:"+joinPoint.getSignature().getDeclaringTypeName());
            //获取所有的请求头
            Enumeration<String> reqHeadInfos = request.getHeaderNames();
            String heads = "";
            while (reqHeadInfos.hasMoreElements()) {
                String headName = reqHeadInfos.nextElement();
                //根据请求头的名字获取对应的请求头的值
                String headValue = request.getHeader(headName);
                heads = heads + headName + "={" + headValue + "}; ";
            }
            log.info("REQUEST-HEAD:" + heads);
            log.info("REQUEST:"+JSON.toJSONString(bean));
        }
    }

    private Logger getLogBean(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(Logger.class);
    }

    @After("log() && @annotation(req)")
    public void doAfter(final Logger req) {
        log.info("==================END REQUEST/REQUEST[Desc:+"+ req.msg() +"]=================");
    }

}
