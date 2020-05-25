package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "积分配置dto")
public class PointsConfigDto {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "消费获取积分比例")
    private BigDecimal consumptionRatio;

    @ApiModelProperty(value = "积分兑换代付金比例")
    private BigDecimal subscriptionRatio;

    @ApiModelProperty(value = "消费获取积分开关")
    private Integer consumptionRatioState;

    @ApiModelProperty(value = "积分兑换代付金开关")
    private Integer subscriptionRatioState;

}
