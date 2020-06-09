package io.treasure.vo;

import com.alibaba.druid.sql.visitor.functions.Char;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderVo {

    private Long id;

    private String orderId;

    private String merchantName;

    private String roomName;

    private Integer reservationType;

    private String reservationName;

    private Integer preferentialType;

    private Long discountId;

    private String eatTime;

    private BigDecimal totalMoney;

    private BigDecimal giftMoney;

    private BigDecimal payMoney;

    private Integer status;

    private char payMode;

    private char invoice;

    private String description;

    private String payDate;

    private String contacts;

    private String contactNumber;

    private Integer checkStatus;

    private char checkMode;

    private String refundReason;

    private String refundId;

    private String refundDate;

    private String createDate;

    private String porderId;

    private BigDecimal merchantProceeds;

    private BigDecimal platformBrokerage;

    private String mobile;

}
