package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户订单管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Data
@TableName("ct_merchant_order")
public class MerchantOrderEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@TableId
	private Long id;
    /**
     * 订单编号
     */
	private String orderNum;
    /**
     * 总金额
     */
	private Double money;
    /**
     * 用餐时间段
     */
	private String times;
    /**
     * 包房/桌
     */
	private Long merchantRoomId;
    /**
     * 支付类型:1-微信,2-支付宝
     */
	private Integer type;
    /**
     * 商户编号
     */
	private Long merchantId;
    /**
     * 用餐时间
     */
	private Date time;
    /**
     * 支付状态
     */
	private Integer payStatus;
    /**
     * 退款/不接受/取消原因
     */
	private String refundReason;
    /**
     * 退款/不接受/取消时间
     */
	private Date refundDate;
    /**
     * 退款/不接受/取消审核人
     */
	private Long verify;
    /**
     * 退款/不接受/取消审核时间
     */
	private Date verifyDate;
	/**
	 * 审核意见
	 */
	private String verifyReason;
    /**
     * 状态
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;
    /**
     * 修改时间
     */
	private Date updateDate;
    /**
     * 修改者
     */
	private Long updater;
    /**
     * 微信ding'dan'hao
     */
	private String transactionid;
    /**
     * 随机字符串
     */
	private String noncestr;
    /**
     * 商户订单号
     */
	private String outtradeno;
    /**
     * 预支付订单号
     */
	private String perpayId;
}