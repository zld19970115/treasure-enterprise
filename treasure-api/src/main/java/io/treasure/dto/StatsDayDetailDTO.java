package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 平台日交易明细表
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-24
 */
@Data
@ApiModel(value = "平台日交易明细表")
public class StatsDayDetailDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private long id;

    /**
     * 交易日期（年月日）
     */
    @ApiModelProperty(value = "交易日期（年月日）")
    private Date createDate;


    /**
     * 交易单号
     */
    @ApiModelProperty(value = "交易单号")
    private String orderId;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式")
    private String payType;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private int incidentType;

    /**
     * 付款手机号
     */
    @ApiModelProperty(value = "付款手机号")
    private String payMobile;

    /**
     * 交易商家
     */
    @ApiModelProperty(value = "交易商家")
    private String payMerchantName;

    /**
     * 交易商家ID
     */
    @ApiModelProperty(value = "交易商家id")
    private long payMerchantId;

    /**
     * 订单总额
     */
    @ApiModelProperty(value = "订单总额")
    private BigDecimal orderTotal;

    /**
     * 商家优惠金额
     */
    @ApiModelProperty(value = "商家优惠金额")
    private BigDecimal merchantDiscountAmount;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal transactionAmount;


    /**
     * 代付金
     */
    @ApiModelProperty(value = "代付金")
    private BigDecimal giftMoney;


    /**
     * 实付金额（总价-优惠券-代付金）
     */
    @ApiModelProperty(value = "实付金额")
    private BigDecimal realityMoney;


    /**
     * 平台佣金
     */
    @ApiModelProperty(value = "平台佣金")
    private BigDecimal platformBrokerage;


    /**
     * 商家所得金额
     */
    @ApiModelProperty(value = "商家所得金额")
    private BigDecimal merchantProceeds;


    /**
     * 提现金额
     */
    @ApiModelProperty(value = "提现金额")
    private BigDecimal withdrawMoney;


    /**
     * 平台余额
     */
    @ApiModelProperty(value = "平台余额")
    private BigDecimal platformBalance;

    /**
     * 微信支付金额
     */
    @ApiModelProperty(value = "微信支付金额")
    private BigDecimal wxPaymoney;

    /**
     * 支付宝支付金额
     */
    @ApiModelProperty(value = "支付宝支付金额")
    private BigDecimal aliPaymoney;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private Date updateDate;

    /**
     * 支付平台手续费
     */
    @ApiModelProperty(value = "支付平台手续费")
    private BigDecimal serviceCharge;
}
