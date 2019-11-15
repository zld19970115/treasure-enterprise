package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "语音推送表")
public class StimmeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "语音推送ID")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private String orderId;
    @ApiModelProperty(value = "商家ID")
    private Long merchantId;

    @ApiModelProperty(value = "类型")
    private Integer type;
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "修改时间")
    private Date updateDate;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "创建者")
    private Long creator;

    @ApiModelProperty(value = "修改者")
    private Long updater;
}
