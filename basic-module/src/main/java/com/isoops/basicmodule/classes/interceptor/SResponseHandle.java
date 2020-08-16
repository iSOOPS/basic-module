package com.isoops.basicmodule.classes.interceptor;

import com.isoops.basicmodule.classes.basicmodel.GenericEnum;
import com.isoops.basicmodule.classes.basicmodel.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@Slf4j
public class SResponseHandle implements ResponseBodyAdvice<Object> {

    /**
     * 是否请求包含了包装注解标记，没有就直接返回，不需要重写返回体
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        //拦截返回对应缓存接口的返回参数
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        //判断请求是否有包装标记
        SResponseUriCache responseUriCache = SResponseUriCache.getInstance();
        String uri = request.getRequestURI();
        return responseUriCache.get(uri);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        //由于使用了@controller，必须手动封装responseBody，然后再取值
        return body;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response<?> handleRuntimeException(Exception exception) {

        if (exception.getCause() instanceof SException){
            SException sException = (SException) exception.getCause();
            if (sException.getGenericEnum()!=null){
                return new Response<>(sException.getGenericEnum());
            }
        }
        return new Response<>(exception.getMessage(),GenericEnum.SYSTEM_ERROR);
    }

//    /**
//     * RequestParam GET请求
//     * 处理普通参数校验失败的异常
//     *
//     * @param exception MethodArgumentNotValidException
//     * @return ResponseVO
//     */
//    @ExceptionHandler(value = MissingServletRequestParameterException.class)
//    @ResponseBody
//    public ResponseVO missingServletRequestParameterException(MissingServletRequestParameterException exception) {
//
//        return ResponseVO.failure(ResultCode.PARAM_IS_INVALID, exception.getParameterName() + "参数错误");
//    }

    /**
     * Valid @RequestBody POST请求
     * 处理普通参数校验失败的异常
     *
     * @param exception MethodArgumentNotValidException
     * @return ResponseVO
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Response<?> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String msg = "提交参数异常";
        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            FieldError error = (FieldError) list.get(0);
            msg = "[" + error.getObjectName() + "." + error.getField() + "] " + error.getDefaultMessage();
        }
        return new Response<>(msg,GenericEnum.FORMAT_ERROR);
    }

}
