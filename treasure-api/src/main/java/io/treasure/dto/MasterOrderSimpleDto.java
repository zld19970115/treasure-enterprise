package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@ApiModel(value = "订单简表")
public class MasterOrderSimpleDto extends MasterOrderExtendsDto {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单号
	 */
	private String orderId;
	/**
	 *
	 */
	private Long roomId;
	/**
	 * 订单描述
	 */
	private String description;

	/**
	 * 联系人
	 */
	private String contacts;
	/**
	 * 联系电话
	 */
	private String contactNumber;

	/**
	 * 订单金额
	 */
	private BigDecimal totalMoney;

	/**
	 * 实际支付金额
	 */
	private BigDecimal payMoney;
	/**
	 * 修改时间
	 */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Date updateDate;

	private BigDecimal pay_coins;

}