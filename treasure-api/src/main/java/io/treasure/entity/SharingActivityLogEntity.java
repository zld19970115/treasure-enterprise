package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sharing_activity_log")
public class SharingActivityLogEntity {

        private Long salId;
        private int activityId;
        private Long initiatorId;
        private String helperMobile;
        private Integer helperValue;
        private Date createPmt;

}
