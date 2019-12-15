package com.isoops.basicmodule.classes.annotation;

import com.alibaba.fastjson.JSON;
import com.isoops.basicmodule.classes.annotation.source.BasicContract;
import com.isoops.basicmodule.classes.annotation.source.InterceptorException;
import com.isoops.basicmodule.classes.basicmodel.GenericEnum;
import com.isoops.basicmodule.classes.basicmodel.Request;
import com.isoops.basicmodule.classes.basicmodel.Response;
import com.isoops.basicmodule.classes.sign.CheckGradeEnum;
import com.isoops.basicmodule.classes.sign.SignGenerater;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class CheckRequestContract extends BasicContract {

    @SuppressWarnings("all")
    @Autowired(required = true)
    private SignGenerater signGenerater;

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(req)")
    public void checkRequest(final JoinPoint joinPoint, CheckRequest req) throws InterceptorException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Request bean = GetArgsModel(joinPoint);
        CheckGradeEnum level = req.level();
        if (bean == null){
            throw new InterceptorException(new Response<>(GenericEnum.FORMAT_ERROR).getMsg());
        }
        checkAction(request,bean,level);
    }

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(req)")
    public void checkRequestData(final JoinPoint joinPoint, CheckRequestData req) throws InterceptorException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String stringData = GetArgsStringData(joinPoint);
        Request bean = JSON.parseObject(stringData,Request.class);
        CheckGradeEnum level = req.level();
        if (stringData == null){
            throw new InterceptorException(new Response<>(GenericEnum.FORMAT_ERROR).getMsg());
        }
        checkAction(request,bean,level);
    }

    private void checkAction(HttpServletRequest request, Request bean, CheckGradeEnum level){
        switch (level) {
            case codeCheck: {
                if (!signGenerater.checkCode(bean.getCode(),request,bean.getUserSignal())) {
                    try {
                        throw new InterceptorException(new Response<>(GenericEnum.SIGN_ERROR).getMsg());
                    } catch (InterceptorException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case signCheck: {
                if (!signGenerater.checkSign(bean.getSign(),bean.getCode())) {
                    try {
                        throw new InterceptorException(new Response<>(GenericEnum.SIGN_ERROR).getMsg());
                    } catch (InterceptorException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}
