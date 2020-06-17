package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import io.treasure.dao.MasterOrderSimpleDao;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_master_order")
public class MasterOrderSimpleEntity extends MasterOrderExtendsEntity {
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

}