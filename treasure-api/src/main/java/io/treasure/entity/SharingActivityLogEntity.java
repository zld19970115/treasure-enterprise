package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sharing_activity_log")
@Accessors(chain = true)
public class SharingActivityLogEntity {

        @TableId(value="sal_id",type = IdType.INPUT)
        private Long salId;
        private Integer activityId;
        private Long initiatorId;
        private Integer proposeSequeueNo;//发起助力顺序号
        private String helperMobile;
        private Integer helperValue;

        @TableField(fill = FieldFill.INSERT)
        private Date createPmt;

}
