package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "商户端pc订单实时VO")
public class RealTimeOrder {

    @ApiModelProperty(value = "预约")
    private Integer completeOrder;

    @ApiModelProperty(value = "进行")
    private Integer conductOrder;

    @ApiModelProperty(value = "申请退款")
    private Integer applyRefundOrder;

    @ApiModelProperty(value = "申请退菜")
    private Integer applyBackDishes;

}
