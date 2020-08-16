package com.isoops.basicmodule.classes.annotation.source;

import com.isoops.basicmodule.classes.basicmodel.Request;
import org.aspectj.lang.JoinPoint;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class BasicContract {

    protected Request<?> GetArgsModel(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Request) {
                return (Request) arg;
            }
        }
        return null;
    }

    protected Request<?> GetArgsWithUrl(HttpServletRequest request){
        Request<?> bean = new Request<>();
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = enu.nextElement();
            if (paraName.equals("code")){
                bean.setCode(request.getParameter(paraName));
            }
            if (paraName.equals("sign")){
                bean.setSign(request.getParameter(paraName));
            }
            if (paraName.equals("userSignal")){
                bean.setUserSignal(request.getParameter(paraName));
            }
        }
        return bean;
    }

    protected String GetUrlParameter(HttpServletRequest request){
        StringBuilder url = new StringBuilder(request.getRequestURL().toString() + "?");
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            url.append(paraName).append("=").append(request.getParameter(paraName)).append("&");
        }
        return url.substring(0,url.length()-1);
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
