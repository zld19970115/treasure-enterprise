package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("coupon_rule")
public class CouponRuleEntity {
    @TableId
    private Long id;
    private Integer subjectType;
    private Integer subjectId;
    private Integer expire_type;
    private Integer keeyLongTime;
    private Date expireTime;
    private Integer consumeLimit;
}
