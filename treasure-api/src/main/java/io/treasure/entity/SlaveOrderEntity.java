package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_slave_order")
public class SlaveOrderEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
	private String orderId;
    /**
     * 菜品ID
     */
	private Long goodId;
    /**
     * 数量
     */
	private BigDecimal quantity;
    /**
     * 单价
     */
	private BigDecimal price;
    /**
     * 总金额
     */
	private BigDecimal totalMoney;
    /**
     * 支付金额
     */
	private BigDecimal payMoney;
    /**
     * 订单菜品状态：1-未支付订单菜品，2-商家已接菜品，3-商户拒接菜品，4-支付完成菜品，5-取消未支付菜品，6-消费者申请退款菜品，7-商户拒绝退款菜品，8-商家同意退款菜品，9-删除订单菜品
     */
	private Integer status;
    /**
     * 订单菜品备注，
     */
	private String description;
    /**
     * 菜品退款原因
     */
	private String refundReason;
    /**
     * 菜品退款单号
     */
	private String refundId;
    /**
     * 菜品退款时间
     */
	private Date refundDate;
    /**
     * 修改时间
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Date updateDate;
    /**
     * 修改者
     */
	@TableField(fill= FieldFill.INSERT_UPDATE)
	private Long updater;

	@TableField(exist=false)
	private GoodEntity goodInfo;

	/**
	 * 赠送金金额（此菜品已经使用的赠送金抵扣的金额）
	 */
	private BigDecimal freeGold;


	/**
	 * 优惠卷金额（此菜品已经使用的优惠卷抵扣的金额）
	 */
	private BigDecimal discountsMoney;


}