package com.isoops.basicmodule.classes.basicmodel;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.isoops.basicmodule.classes.ErrorTemp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by samuel on 2018/6/21.
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
        setMsgGeneric(GenericEnum.SUCESS);
        this.state = true;
        this.stateCode = 200;
    }

    public Response(GenericEnum genericEnum){
        this.state = genericEnum == GenericEnum.SUCESS;
        this.stateCode = genericEnum == GenericEnum.SUCESS ? 200 : 500;
        setMsgGeneric(genericEnum);
    }


    public Response(String msg,GenericEnum genericEnum){
        this.state = genericEnum == GenericEnum.SUCESS;
        this.stateCode = genericEnum == GenericEnum.SUCESS ? 200 : 500;
        this.msg = msg;
    }


    public Response(T object){
        this.object = object;
        setMsgGeneric(GenericEnum.SUCESS);
        this.state = true;
        this.stateCode = 200;
    }

    public Response(IPage<T> page){
        this.object = (T) page.getRecords();
        this.pageCount = getPageSizeCount(page.getTotal(),page.getSize());
        this.haveNext = (page.getCurrent() + 1) < getPageCount();
        setMsgGeneric(GenericEnum.SUCESS);
        this.state = true;
        this.stateCode = 200;
    }



    private Long getPageSizeCount(Long allCount,Long pageSize){
        if (allCount==null || pageSize == null){
            return 0L;
        }
        float i = allCount%pageSize;
        if (i == 0){
            return allCount/pageSize;
        }
        return allCount/pageSize+1;
    }


    private void setMsgGeneric(GenericEnum genericEnum){
        switch (genericEnum){
            case FORMAT_ERROR:{
                this.msg = ErrorTemp.DATA_FORMAT_ERROR;
                break;
            }
            case ACTION_ERROR:{
                this.msg = ErrorTemp.ACTION_FALE;
                break;
            }
            case SYSTEM_ERROR:{
                this.msg = ErrorTemp.SYSTEM_ERROR;
                break;
            }
            case SIGN_ERROR:{
                this.msg = ErrorTemp.SIGN_ERROR;
                break;
            }
            case PERMISSION_ERROR:{
                this.msg = ErrorTemp.PERMISSION_FALE;
                break;
            }
            case REPETITION_ERROR:{
                this.msg = ErrorTemp.REPETITION_ERROR;
                break;
            }
            default:{
                this.msg = "成功";
            }
        }
    }



}
