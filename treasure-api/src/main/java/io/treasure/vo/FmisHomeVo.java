package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "财务首页VO")
public class FmisHomeVo {

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalCash;

    @ApiModelProperty(value = "商家收入总金额")
    private BigDecimal merchantTotalCash;

    @ApiModelProperty(value = "平台收入总金额")
    private BigDecimal pointMoney;

    @ApiModelProperty(value = "余额支付总金额")
    private BigDecimal balanceTotalCash;

    @ApiModelProperty(value = "支付宝支付总金额")
    private BigDecimal aliTotalCash;

    @ApiModelProperty(value = "微信支付总金额")
    private BigDecimal wxTotalCash;

    @ApiModelProperty(value = "商家总数")
    private Integer merchantCount;

    @ApiModelProperty(value = "用户总数")
    private Integer userCount;

    @ApiModelProperty(value = "主订单总数")
    private Integer pOrderCount;

    @ApiModelProperty(value = "子订单总数")
    private Integer orderCount;

    @ApiModelProperty(value = "代付金")
    private BigDecimal giftTotalCash;

}
