package com.isoops.basicmodule.classes.basicmodel;

import org.springframework.http.HttpStatus;

/**
 * Created by samuel on 2018/6/29.
 */
public enum  GenericEnum {

    SUCESS(200,"OK",HttpStatus.OK),
    FORMAT_ERROR(400,"提交参数结构不正确,拒绝请求",HttpStatus.BAD_REQUEST),
    SIGN_ERROR(401,"签名异常,拒绝请求",HttpStatus.UNAUTHORIZED),
    PERMISSION_ERROR(403,"权限异常,拒绝请求",HttpStatus.FORBIDDEN),
    SYSTEM_ERROR(500,"系统出错",HttpStatus.INTERNAL_SERVER_ERROR),
    REPETITION_ERROR(503,"操作过于频繁，请等待响应或重新请求",HttpStatus.SERVICE_UNAVAILABLE),

    DATABASE_ERROR(3306,"数据库异常",HttpStatus.BAD_REQUEST),
    NULL_ERROR(3000,"不能为空",HttpStatus.BAD_REQUEST),
    CHECK_CODE_ERROR(3100,"验证码错误",HttpStatus.BAD_REQUEST),

    ;



    private final int value;
    private final String reasonPhrase;
    private HttpStatus httpStatus;

    private GenericEnum(int value, String reasonPhrase,HttpStatus httpStatus) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
        this.httpStatus = httpStatus;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
