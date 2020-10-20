package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.math.BigDecimal;
import java.util.List;

/**
 * 计算赠送金和优惠卷条件
 */
@Data
@ApiModel(value = "计算赠送金和优惠卷条件")
public class DesignConditionsDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "扣减金额类型：0默认未扣减、1赠送金扣减、2优惠卷扣减、3优惠卷与赠送金同时使用")
    private Integer preferentialType;

    @ApiModelProperty(value = "订单菜品")
    private List<calculationAmountDTO> slaveOrder;

    @ApiModelProperty(value = "用户id")
    private long userId;

    @ApiModelProperty(value = "ID")
    private long id;

    @ApiModelProperty(value = "mid")//商户id
    private long mid;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalMoney;


}
