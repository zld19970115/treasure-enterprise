package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@ApiModel(value = "订单菜品表")
public class SlaveOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "订单菜品ID")
	private Long id;

	@ApiModelProperty(value = "订单号")
	private String orderId;

	@ApiModelProperty(value = "菜品ID")
	private Long goodId;

	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;

	@ApiModelProperty(value = "退款数量")
	private BigDecimal refundQuantity;

	@ApiModelProperty(value = "单价")
	private BigDecimal price;

	@ApiModelProperty(value = "总金额")
	private BigDecimal totalMoney;

	@ApiModelProperty(value = "支付金额")
	private BigDecimal payMoney;

	@ApiModelProperty(value = "订单菜品状态：1-未支付订单菜品，2-商家已接菜品，3-商户拒接菜品，4-支付完成菜品，5-取消未支付菜品，6-消费者申请退款菜品，7-商户拒绝退款菜品，8-商家同意退款菜品，9-删除订单菜品")
	private Integer status;

	@ApiModelProperty(value = "订单菜品备注，")
	private String description;

	@ApiModelProperty(value = "菜品退款原因")
	private String refundReason;

	@ApiModelProperty(value = "菜品退款单号")
	private String refundId;

	@ApiModelProperty(value = "菜品退款时间")
	private Date refundDate;

	@ApiModelProperty(value = "修改时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "修改者")
	private Long updater;

	@ApiModelProperty(value = "赠送金金额（此菜品已经使用的赠送金抵扣的金额")
	private BigDecimal freeGold;

	@ApiModelProperty(value = "优惠卷金额（此菜品已经使用的优惠卷抵扣的金额）")
	private BigDecimal discountsMoney;

	@ApiModelProperty(value = "商户Id")
	private String merchantId;
	@ApiModelProperty(value = "菜品名称")
	private String cgName;
	@ApiModelProperty(value = "菜品图片")
	private String cgeIcon;
}