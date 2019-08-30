package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户优惠卷表
 * 2019.8.28
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_user_coupon")
public class UserCouponEntity {
    /**
     * ID
     */
    private long Id;
    /**
     * .用户id
     */
    private  long userId;
    /**
     * 用户卷id
     */
    private long couponId;

    /**
     * 商家id
     */
    private long martId;

    /**
     * 状态 0=已使用,1=未使用
     */
    private Integer status;
    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建者
     */
    private long creator;
    /**
     * 修改者
     */
    private long updater;
}
