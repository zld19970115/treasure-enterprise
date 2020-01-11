package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "平台商户天合计")
public class CtDaysTogetherDTO {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private Date payDates;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private long merchantId;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式")
    private String payType;


    /**
     * 支付平台手续费总额
     */
    @ApiModelProperty(value = "支付平台手续费总额")
    private BigDecimal serviceChanrge;

    /**
     * 订单总额
     */
    @ApiModelProperty(value = "订单总额")
    private BigDecimal orderTotal;

    /**
     * 商户所得金额总额
     */
    @ApiModelProperty(value = "商户所得金额总额")
    private BigDecimal merchantProceeds;

    /**
     * 平台扣点金额总额
     */
    @ApiModelProperty(value = "平台扣点金额总额")
    private BigDecimal platformBrokerage;

    /**
     * 实付金额（总价-优惠券-代付金）
     */
    @ApiModelProperty(value = "实付金额（总价-优惠券-代付金）")
    private BigDecimal realityMoney;
    /**
     * 商家优惠金额
     */
    @ApiModelProperty(value = "商家优惠金额")
    private BigDecimal merchantDiscountAmount;
    /**
     * 代付金
     */
    @ApiModelProperty(value = "代付金")
    private BigDecimal giftMoney;

}
