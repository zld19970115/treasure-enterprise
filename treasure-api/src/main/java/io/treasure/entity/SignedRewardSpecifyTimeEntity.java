package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("signed_reward_specify_time")
public class SignedRewardSpecifyTimeEntity {

    @TableId
    private Long id;                       //
    private Integer rewardType;            // int null comment '1宝币',
    private Integer rewardValue;           // int null comment '奖励值',
    private Integer personAmount;          // int null comment '人员数量',
    private Date startPmt;                 // datetime;// null comment '开始时间标记',
    private Date endingPmt;                // datetime null comment '结束时间',
    private Integer times;
    private Integer maxValue;
    private Integer minValue;
    private Integer activityMode;           //1数量模式，2仅范围模式
    private Integer onceInTimeRange;        //
    private Long couponActivityId;          //

    /*
    alter table signed_reward_specify_time modify times int null comment '单次活动的次数限制';

    alter table signed_reward_specify_time
        add times_in_time_range int null comment '多长时间才可以抢一次' after times;

    alter table signed_reward_specify_time
        add coupon_activity_id bigint null comment '活动id' after times_in_time_range;
     */

}
