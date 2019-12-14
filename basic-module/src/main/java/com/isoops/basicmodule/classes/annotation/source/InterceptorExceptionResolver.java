package com.isoops.basicmodule.classes.annotation.source;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.isoops.basicmodule.classes.basicmodel.Response;
import com.isoops.basicmodule.source.SBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class InterceptorExceptionResolver implements HandlerExceptionResolver {

    //异常拦截器
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Object o,
                                         Exception e) {
        String msg = "";
        Integer statusCode;
        if (e.getCause() instanceof InterceptorException) {
            msg = (e.getCause().getMessage() != null) ? e.getCause().getMessage() : "unkown error";
            statusCode = HttpStatus.BAD_REQUEST.value();
        } else {
            msg = msg + e.getMessage() + "||";
            for (int i = 0; i < (e.getStackTrace().length > 3 ? 3 : e.getStackTrace().length); i++) {
                msg = msg + "[" + e.getStackTrace()[i].getClassName() + ":" + e.getStackTrace()[i].getLineNumber() + "]";
            }
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        ModelAndView mv = new ModelAndView();
        FastJsonJsonView view = new FastJsonJsonView();

        Response model = new Response<>();
        model.setStateCode(statusCode);
        model.setState(false);
        model.setMsg(msg);

        Map<String, ?> attributes = SBean.beanToMap(model);
        view.setAttributesMap(attributes);
        mv.setView(view);

        return mv;
    }
}
