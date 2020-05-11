package io.treasure.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DaysTogetherStatisticsVo {

    @ApiModelProperty(value = "余额数量")
    private Integer banalceCount;

    @ApiModelProperty(value = "支付宝数量")
    private Integer alipayCount;

    @ApiModelProperty(value = "微信数量")
    private Integer wxpayCount;

    @ApiModelProperty(value = "银行卡数量")
    private Integer bankcardCount;

    @ApiModelProperty(value = "订单总额")
    private BigDecimal orderTotal;

    @ApiModelProperty(value = "支付平台手续费总额")
    private BigDecimal serviceChanrge;

    @ApiModelProperty(value = "商户所得金额总额")
    private BigDecimal merchantProceeds;

    @ApiModelProperty(value = "平台扣点金额总额")
    private BigDecimal platformBrokerage;

    @ApiModelProperty(value = "实付金额（总价-优惠券-代付金）")
    private BigDecimal realityMoney;

    @ApiModelProperty(value = "商家优惠金额")
    private BigDecimal merchantDiscountAmount;

    @ApiModelProperty(value = "代付金")
    private BigDecimal giftMoney;

}
