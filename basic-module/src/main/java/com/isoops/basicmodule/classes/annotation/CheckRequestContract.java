package com.isoops.basicmodule.classes.annotation;

import com.alibaba.fastjson.JSON;
import com.isoops.basicmodule.classes.annotation.source.BasicContract;
import com.isoops.basicmodule.classes.annotation.source.InterceptorException;
import com.isoops.basicmodule.classes.basicmodel.GenericEnum;
import com.isoops.basicmodule.classes.basicmodel.Request;
import com.isoops.basicmodule.classes.basicmodel.Response;
import com.isoops.basicmodule.classes.sign.CheckGradeEnum;
import com.isoops.basicmodule.classes.sign.SignGenerater;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class CheckRequestContract extends BasicContract {

    @SuppressWarnings("all")
    @Autowired(required = true)
    private SignGenerater signGenerater;

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(req)")
    public void checkRequest(final JoinPoint joinPoint, CheckRequest req) throws InterceptorException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (req.ignore()) {
            log.warn("温馨提示:"+request.getRequestURI()+"停用了code/sign校验");
            return;
        }
        Request bean = GetArgsModel(joinPoint);
        if (bean == null){
            throw new InterceptorException(new Response<>(GenericEnum.FORMAT_ERROR).getMsg());
        }
        if (!checkAction(request,bean,req.level())){
            throw new InterceptorException(new Response<>(GenericEnum.SIGN_ERROR).getMsg());
        }
    }

    @Before("within(@org.springframework.stereotype.Controller *) && @annotation(req)")
    public void checkRequestUrl(CheckRequestUrl req) throws InterceptorException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (req.ignore()){
            log.warn("温馨提示:"+request.getRequestURI()+"停用了code/sign校验");
            return;
        }
        Request bean = GetArgsWithUrl(request,req.codeKey(),req.signKey(),req.userSignalKey());
        if (!checkAction(request,bean,req.level())){
            throw new InterceptorException(new Response<>(GenericEnum.SIGN_ERROR).getMsg());
        }
    }

    private boolean checkAction(HttpServletRequest request, Request bean, CheckGradeEnum level){
        switch (level) {
            case codeCheck: {
                return signGenerater.checkCode(bean.getCode(),request,bean.getUserSignal());
            }
            case signCheck: {
                return signGenerater.checkSign(bean.getCode(),bean.getSign());
            }
            default:
                return false;
        }
    }
}
