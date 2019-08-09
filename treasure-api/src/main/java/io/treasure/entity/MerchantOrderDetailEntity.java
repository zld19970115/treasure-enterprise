package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户订单明细管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Data
@TableName("ct_merchant_order_detail")
public class MerchantOrderDetailEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@TableId
	private Long id;
    /**
     * 订单号
     */
	private Long orderId;
    /**
     * 菜品编号
     */
	private Long goodId;
    /**
     * 菜品数量
     */
	private Integer num;
    /**
     * 菜品价格
     */
	private Double price;
    /**
     * 
     */
	private Long merchantId;
    /**
     * 订单状态
     */
	private Integer payStatus;
    /**
     * 退菜原因
     */
	private String refundReason;
    /**
     * 退菜时间
     */
	private Date refundDate;
    /**
     * 审核人
     */
	private Long verify;
    /**
     * 审核时间
     */
	private Date verifyDate;
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
}