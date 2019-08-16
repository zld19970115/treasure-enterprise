package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.entity.MerchantOrderDetailEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 商户订单管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Data
@ApiModel(value = "商户订单管理")
public class MerchantOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "订单编号")
	private String orderNum;

	@ApiModelProperty(value = "总金额")
	private Double money;

	@ApiModelProperty(value = "用餐时间段")
	private String times;

	@ApiModelProperty(value = "包房/桌")
	private Long merchantRoomId;

	@ApiModelProperty(value = "支付类型:1-微信,2-支付宝")
	private Integer type;

	@ApiModelProperty(value = "商户编号")
	private Long merchantId;

	@ApiModelProperty(value = "用餐时间")
	private Date time;

	@ApiModelProperty(value = "支付状态")
	private Integer payStatus;

	@ApiModelProperty(value = "退款/不接受/取消原因")
	private String refundReason;

	@ApiModelProperty(value = "退款/不接受/取消时间")
	private Date refundDate;

	@ApiModelProperty(value = "退款/不接受/取消审核人")
	private Long verify;

	@ApiModelProperty(value = "退款/不接受/取消审核时间")
	private Date verifyDate;

	@ApiModelProperty(value = "状态")
	private Integer status;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "修改时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "修改者")
	private Long updater;

	@ApiModelProperty(value = "微信ding'dan'hao")
	private String transactionid;

	@ApiModelProperty(value = "随机字符串")
	private String noncestr;

	@ApiModelProperty(value = "商户订单号")
	private String outtradeno;

	@ApiModelProperty(value = "预支付订单号")
	private String perpayId;
	@ApiModelProperty(value = "订单明细")
	private List<MerchantOrderDetailEntity> detailList;

}