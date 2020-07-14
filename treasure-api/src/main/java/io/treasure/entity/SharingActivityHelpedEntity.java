package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sharing_activity_helped")
@Accessors(chain = true)
public class SharingActivityHelpedEntity {

        @TableId(value="sal_id",type = IdType.INPUT)
        private Long salId;
        private Integer activityId;
        private Long initiatorId;
        private Integer proposeSequeueNo;//发起助力顺序号
        private String helperMobile;
        private Integer helperValue;

        @TableField(fill = FieldFill.INSERT)
        private Date createPmt;

        //附加自ct_client_user
        private String userName;
        //附加自ct_client_user
        private String headImg;

}
