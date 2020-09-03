package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("coupon_rule")
public class CouponRuleEntity {

    private Integer id;
    private Integer subjectType;
    private Integer subjectId;
    private Integer expire_type;
    private Integer keeyLongTime;
}
