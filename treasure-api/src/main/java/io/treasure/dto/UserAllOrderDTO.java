package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Data
@ApiModel(value = "用户端全部列表")
public class UserAllOrderDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "商家ID")
    private Long merchantId;

    @ApiModelProperty(value = " ")
    private Long roomId;

    @ApiModelProperty(value = "预订类型：1、正常预订；2、只预订包房/散台，3、只预订菜品")
    private Integer reservationType;

    @ApiModelProperty(value = "预订时间ID")
    private Long reservationId;

    @ApiModelProperty(value = "订单优惠ID")
    private Long discountId;

    @ApiModelProperty(value = "就餐时间")
    private Date eatTime;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal totalMoney;

    @ApiModelProperty(value = "赠送金额")
    private BigDecimal giftMoney;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "订单状态：1-未支付订单，2-商家已接单，3-商户拒接单，4-支付完成订单，5-取消未支付订单，6-消费者申请退款订单，7-商户拒绝退款订单，8-商家同意退款订单，9-删除订单")
    private Integer status;

    @ApiModelProperty(value = "支付方式：1-余额支付，2-支付宝支付，3-微信支付，4-银行卡支付")
    private String payMode;

    @ApiModelProperty(value = "发票是否已开：0-未开，1-已开")
    private String invoice;

    @ApiModelProperty(value = "订单描述")
    private String description;

    @ApiModelProperty(value = "支付时间")
    private Date payDate;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "联系电话")
    private String contactNumber;

    @ApiModelProperty(value = "结账状态：0-未结账，1-已结账")
    private Integer checkStatus;

    @ApiModelProperty(value = "结账方式：1-消费者结账，2-商家结账，3-系统结账。")
    private String checkMode;

    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    @ApiModelProperty(value = "退款单号")
    private String refundId;

    @ApiModelProperty(value = "退款时间")
    private Date refundDate;

    @ApiModelProperty(value = "修改时间")
    private Date updateDate;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "创建者")
    private Long creator;

    @ApiModelProperty(value = "修改者")
    private Long updater;

    @ApiModelProperty(value = "加菜")
    private  String pOrderId;
    @ApiModelProperty(value = "总支付金额")
    private  BigDecimal allPaymoney;


    @ApiModelProperty(value = "主单相关联的所有单的实付金额总和")
    private BigDecimal allpaymoneys;


    @ApiModelProperty(value = "平台扣点金额")
    private BigDecimal platformBrokerage;


    @ApiModelProperty(value = "商户实际所得金额（扣除平台扣点不包含赠送金）")
    private BigDecimal merchantProceeds;

    @ApiModelProperty(value = "商户实际所得金额（扣除平台扣点不包含赠送金）")
    private BigDecimal discountsMoney;
//————————————————————————————————————————————————————
    @ApiModelProperty(value = "菜品數量")
    private Integer goodNum;

    @ApiModelProperty(value = "菜品图片")
    private String goodIcon;

    @ApiModelProperty(value = "包房图片")
    private String roomIcon;

    @ApiModelProperty(value = "包房押金")
    private BigDecimal roomMoney;

    @ApiModelProperty(value = "商户名")
    private String merchantName;

    @ApiModelProperty(value = "菜名")
    private String goodName;


}
