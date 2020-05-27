package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.treasure.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.text.Bidi;
import java.util.Date;

@Data
@TableName("ct_activity_give")
public class ActivityGiveEntity extends BaseEntity {

    @TableId
    private Long id;

    private Long activityId;

    private Integer type;

    private Integer initNum;

    private Integer receiveNum;

    private BigDecimal totalCost;

    private BigDecimal cost;

    private Date createDate;

    private Date updateDate;

    private Long creator;

    private Long updater;

}
