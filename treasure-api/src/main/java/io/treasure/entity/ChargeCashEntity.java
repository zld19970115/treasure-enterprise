package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 现金充值表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2020/3、23
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_charge_cash")
public class ChargeCashEntity  extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private String cashOrderId;

    private long userId;
    /**
     * 存入现金金额
     */
    private BigDecimal cash;
    /**
     * 赠送代付金金额
     */
    private BigDecimal changeGift;
    /**
     * 存入时间
     */
    private Date saveTime;
    /**
     * 状态
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
    private Long creator;
    /**
     * 修改者
     */
    private Long updater;
}
