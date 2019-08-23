package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@TableName("ct_merchant_coupon")
public class MerchantCouponEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
	private String name;
    /**
     * 类型1-满减卷，2-满赠卷，3-优惠卷
     */
	private Integer type;
    /**
     * 满多少金额可以使用优惠卷
     */
	private Double money;
    /**
     * 优惠金额
     */
	private Double amount;
    /**
     * 优惠折扣u
     */
	private Double discount;
    /**
     * 开始时间
     */
	private Date startDate;
    /**
     * 结束时间
     */
	private Date endDate;
    /**
     * 商户
     */
	private Long merchantId;
    /**
     * 备注
     */
	private String remark;
    /**
     * 9-删除，1-显示，0-隐藏
     */
	private Integer status;
    /**
     * 修改时间
     */
	private Date updateDate;
    /**
     * 修改者
     */
	private Long updater;
    /**
     * 1-可重复领取，2-不可重复领取
     */
	private Integer isRepeat;
    /**
     * 发放条件:1-自己领取,2-满额自动领取，3-自动发放
     */
	private Integer grants;
}