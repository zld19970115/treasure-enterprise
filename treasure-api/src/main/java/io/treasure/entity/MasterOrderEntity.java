package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
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
public class MasterOrderEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
	private String orderId;
    /**
     * 商家ID
     */
	private Long merchantId;
    /**
     *  
     */
	private Long roomId;
    /**
     * 预订类型：1、正常预订；2、只预订包房/散台，3、只预订菜品
     */
	private Integer reservationType;
    /**
     * 预订时间ID
     */
	private Long reservationId;
    /**
     * 订单优惠ID
     */
	private Long discountId;
    /**
     * 就餐时间
     */
	private Date eatTime;
    /**
     * 订单金额
     */
	private BigDecimal totalMoney;
	/**
	 * 赠送金额
	 */
	private BigDecimal giftMoney;
    /**
     * 实际支付金额
     */
	private BigDecimal payMoney;
    /**
     * 订单状态：1-未支付订单，2-商家已接单，3-商户拒接单，4-支付完成订单，5-取消未支付订单，6-消费者申请退款订单，7-商户拒绝退款订单，8-商家同意退款订单，9-删除订单
     */
	private Integer status;
    /**
     * 支付方式：1-余额支付，2-支付宝支付，3-微信支付，4-银行卡支付
     */
	private String payMode;
    /**
     * 发票是否已开：0-未开，1-已开
     */
	private String invoice;
    /**
     * 订单描述
     */
	private String description;
    /**
     * 支付时间
     */
	private Date payDate;
    /**
     * 联系人
     */
	private String contacts;
	/**
	 * 业务员查看状态 0--未查看 1--已查看
	 */
	private Integer bmStatus;
	/**
	 * 是否发过提醒短信 0---没发过 0--发过
	 */
	private Integer smsStatus;
    /**
     * 联系电话
     */
	private String contactNumber;
	/**
	 * 逻辑删除字段
	 */
	@TableLogic
	private Integer deleted;
    /**
     * 结账状态：0-未结账，1-已结账
     */
	private Integer checkStatus;
    /**
     * 结账方式：1-消费者结账，2-商家结账，3-系统结账。
     */
	private String checkMode;
    /**
     * 退款原因
     */
	private String refundReason;
    /**
     * 退款单号
     */
	private String refundId;
    /**
     * 退款时间
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

	private Long creator;

	@TableField(exist=false)
	private MerchantEntity merchantInfo;

	@TableField(exist=false)
	private List<SlaveOrderEntity> slaveOrder;

	private Integer responseStatus;
	/**
	 * 扣减金额类型：0默认未扣减、1赠送金扣减、2优惠卷扣减、3优惠卷与赠送金同时使用
	 */
	private Integer preferentialType;
	/**
	 * 加菜订单的orderId
	 */
	private String pOrderId;

	/**
	 * 平台扣点金额
	 */
	private BigDecimal platformBrokerage;

	/**
	 * 商户实际所得金额（扣除平台扣点不包含赠送金）
	 */
	private BigDecimal merchantProceeds;

	private BigDecimal pay_coins;



}