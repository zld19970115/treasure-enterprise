package io.treasure.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StatSdayDetailPageVo {

    private Long id;

    private String createDate;

    private String orderId;

    private Integer payType;

    private Integer incidentType;

    private String payMobile;

    private String payMerchantName;

    private String payMerchantId;

    private BigDecimal orderTotal;

    private BigDecimal merchantDiscountAmount;

    private BigDecimal transactionAmount;

    private BigDecimal giftMoney;

    private BigDecimal realityMoney;

    private BigDecimal platformBrokerage;

    private BigDecimal merchantProceeds;

    private BigDecimal withdrawMoney;

    private BigDecimal platformBalance;

    private BigDecimal wxPaymoney;

    private BigDecimal aliPaymoney;

    private BigDecimal serviceCharge;

    private String updateDate;

    private String tgoodOrderId;

    private String torderId;

    private BigDecimal realityMoneyNew;

    private BigDecimal yePaymoney;

}
