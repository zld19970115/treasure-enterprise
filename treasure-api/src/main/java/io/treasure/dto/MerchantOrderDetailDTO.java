package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 商户订单明细管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Data
@ApiModel(value = "商户订单明细管理")
public class MerchantOrderDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "订单号")
	private Long orderId;

	@ApiModelProperty(value = "菜品编号")
	private Long goodId;

	@ApiModelProperty(value = "菜品数量")
	private Integer num;

	@ApiModelProperty(value = "菜品价格")
	private Double price;

	@ApiModelProperty(value = "")
	private Long merchantId;

	@ApiModelProperty(value = "订单状态")
	private Integer payStatus;

	@ApiModelProperty(value = "退菜原因")
	private String refundReason;

	@ApiModelProperty(value = "退菜时间")
	private Date refundDate;

	@ApiModelProperty(value = "审核人")
	private Long verify;

	@ApiModelProperty(value = "审核时间")
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


}