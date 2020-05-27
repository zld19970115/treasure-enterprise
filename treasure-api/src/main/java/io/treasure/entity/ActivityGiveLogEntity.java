package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("ct_activity_give_log")
public class ActivityGiveLogEntity extends BaseEntity {

    @TableId
    private Long id;

    private Long activityId;

    private Long userId;

    private Integer type;

    private BigDecimal cost;

    private Date createDate;

    private Long giveId;

}
