package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 平台日交易明细表
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-24
 */
@Data
@TableName("stats_day_detail")
public class StatsDayDetailEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 交易日期（年月日）
     */
    private Date createDate;


    /**
     * 交易单号
     */
    private String orderId;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 业务类型
     */
    private int incidentType;

    /**
     * 付款手机号
     */
    private String payMobile;

    /**
     * 交易商家
     */
    private String payMerchantName;


    /**
     * 交易商家ID
     */
    private long payMerchantId;

    /**
     * 订单总额
     */
    private BigDecimal orderTotal;

    /**
     * 商家优惠金额
     */
    private BigDecimal merchantDiscountAmount;

    /**
     * 交易金额
     */
    private BigDecimal transactionAmount;


    /**
     * 代付金
     */
    private BigDecimal giftMoney;


    /**
     * 实付金额（总价-优惠券-代付金）
     */
    private BigDecimal realityMoney;


    /**
     * 平台佣金
     */
    private BigDecimal platformBrokerage;


    /**
     * 商家所得金额
     */
    private BigDecimal merchantProceeds;


    /**
     * 提现金额
     */
    private BigDecimal withdrawMoney;


    /**
     * 平台余额
     */
    private BigDecimal platformBalance;

    /**
     * 微信支付金额
     */
    private BigDecimal wxPaymoney;

    /**
     * 支付宝支付金额
     */
    private BigDecimal aliPaymoney;

    /**
     * 最后更新时间
     */
    private Date updateDate;

    /**
     * 支付平台手续费
     */
    private BigDecimal serviceCharge;


}
