package com.isoops.basicmodule.classes.basicmodel;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;

/**
 * Created by samuel on 2020/08/16
 */
@ApiModel(value = "Response",reference = "Response")
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> implements Serializable {

    @ApiModelProperty(value = "状态")
    private Boolean state;

    @ApiModelProperty(value = "状态描述")
    private String msg;

    @ApiModelProperty(value = "状态码")
    private Integer stateCode;

    @ApiModelProperty(value = "是否有下一页")
    private Boolean haveNext;

    @ApiModelProperty(value = "分页总数量")
    private Long pageCount;

    @ApiModelProperty(value = "返回对象")
    private T object;

    public Response(){
        this.state = true;
        this.stateCode = GenericEnum.SUCESS.value();
        this.msg = GenericEnum.SUCESS.getReasonPhrase();
    }

    public Response(GenericEnum genericEnum){
        this.state = genericEnum == GenericEnum.SUCESS;
        this.stateCode = genericEnum.value();
        this.msg = genericEnum.getReasonPhrase();
    }


    public Response(String msg,GenericEnum genericEnum){
        this.state = genericEnum == GenericEnum.SUCESS;
        this.stateCode = genericEnum.value();
        this.msg = msg!=null && msg.length()>0 ? msg : genericEnum.getReasonPhrase();
    }


    public Response(T object){
        this.state = true;
        this.stateCode = GenericEnum.SUCESS.value();
        this.msg = GenericEnum.SUCESS.getReasonPhrase();
        this.object = object;
    }

    public Response<T> toPage(IPage<?> page){
        this.pageCount = page.getPages();
        this.haveNext = (page.getCurrent() + 1) <= page.getPages();
        return this;
    }
}
