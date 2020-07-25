package com.isoops.basicmodule.classes.basicmodel;

/**
 * Created by samuel on 2018/6/29.
 */
public enum  GenericEnum {

    SUCESS,

    FORMAT_ERROR,           //提交参数结构不正确
    ACTION_ERROR,           //操作异常
    SYSTEM_ERROR,           //系统出错
    SIGN_ERROR,             //签名异常
    PERMISSION_ERROR,       //权限异常
    REPETITION_ERROR,       //操作过于频繁，请等待响应或重新请求
    DATABASE_ERROR,         //数据库异常

}
