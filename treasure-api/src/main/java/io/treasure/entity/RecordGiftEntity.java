package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("ct_record_gift")
public class RecordGiftEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 记录ID
     */
    private long id;
    /**
     * 用户ID
     */
    private long userId;
    /**
     * 使用或充值得赠送金
     */
    private BigDecimal useGift;
    /**
     * 赠送金余额
     */
    private BigDecimal balanceGift;
    /**
     * 状态0---充值类型   1-----使用类型
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
