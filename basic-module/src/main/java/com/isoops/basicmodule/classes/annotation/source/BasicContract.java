package com.isoops.basicmodule.classes.annotation.source;

import com.isoops.basicmodule.classes.basicmodel.Request;
import org.aspectj.lang.JoinPoint;

import javax.servlet.http.HttpServletRequest;

public class BasicContract {

    protected Request GetArgsModel(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Request){
                return (Request) args[i];
            }
        }
        return null;
    }

    protected String GetArgsStringData(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String){
                return (String) args[i];
            }
        }
        return null;
    }

    //获取真实的ip
    protected String GetIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    //获取调用接口名称
    protected String GetInterfaceName(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String[] sArray = uri.split("/");
        if (sArray.length > 2) {
            return sArray[2].substring(0, sArray[2].length() - 4);
        }
        return null;
    }
}
