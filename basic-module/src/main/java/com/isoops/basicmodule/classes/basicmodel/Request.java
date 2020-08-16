package com.isoops.basicmodule.classes.basicmodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Created by samuel on 2018/6/14.
 */
@ApiModel(value = "Request",reference = "Request")
@Data
public class Request<T> implements Serializable {

    @ApiModelProperty(value = "加密签名key码", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @ApiModelProperty(value = "加密签名", required = true)
    @NotBlank(message = "sign不能为空")
    private String sign;

    @ApiModelProperty(value = "用户标示", required = true)
    @NotBlank(message = "用户标示不能为空")
    private String userSignal;

    @ApiModelProperty(value = "数据对象", required = true)
    @Valid
    private T object;

    @ApiModelProperty(value = "分页index")
    private Integer pageIndex;

    @ApiModelProperty(value = "分页size")
    private Integer pageSize;

    @ApiModelProperty(value = "渠道", required = true)
    private Integer channel;

    @ApiModelProperty(value = "版本号", required = true)
    private String version;
}
