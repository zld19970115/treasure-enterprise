package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("signed_reward_specify_time")
public class SignedRewardSpecifyTimeEntity {

    @TableId
    private Long id;                        //
    private Integer reward_type;            // int null comment '1宝币',
    private Integer reward_value;           // int null comment '奖励值',
    private Integer person_amount;          // int null comment '人员数量',
    private Date start_pmt;                 // datetime;// null comment '开始时间标记,每天',
    private Date ending_pmt;                // datetime null comment '结束时间,每天',
    private Integer times;

}
