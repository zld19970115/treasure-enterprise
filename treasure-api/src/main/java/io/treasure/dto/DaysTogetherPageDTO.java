package io.treasure.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DaysTogetherPageDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "支付时间")
    private Date payDate;

    @ApiModelProperty(value = "商户id")
    private Long merchantId;

    @ApiModelProperty(value = "支付方式：1-余额支付，2-支付宝支付，3-微信支付，4-银行卡支付")
    private String payType;

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

    @ApiModelProperty(value = "商家信息")
    private MerchantDTO merchantInfo;

    @ApiModelProperty(value = "商家id")
    private Long mid;

    @ApiModelProperty(value = "商家名称")
    private String name;

    @ApiModelProperty(value = "平台实际所得")
    private BigDecimal platformRealityMoney;

    private BigDecimal payCoins;

    private BigDecimal realityMoneyNew;

}
