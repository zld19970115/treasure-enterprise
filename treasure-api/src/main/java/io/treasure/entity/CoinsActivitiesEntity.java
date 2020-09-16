package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("coins_activities")
public class CoinsActivitiesEntity {

    @TableId
    private Long id;                    //          bigint auto_increment
    private Integer type;               //          int default 0     null comment '1宝币活动',
    private Integer status;             //          int default 1     null comment '1有效，2无效',
    private Integer mode;               //          int default 1     null comment '1常规，2忽略人数',
    private Integer headPersonNum;    //          int default 10    null comment '首段人数',
    private Integer firstPrizeNum;    //          int default 1     null comment '头奖数',
    private Integer firstPrizePosS;  //          int default 0     null comment '头奖出现的开始位置（包含）',
    private Integer firstPrizePosE;  //          int default 0     null comment '头奖出现的结束位置（包含）',
    private Integer prizeMaxmum;       //          int default 50    null comment '头奖高限',
    private Integer prizeMinmun;       //          int default 30    null comment '头奖低限',
    private Integer bodySegmentNum;   //          int default 10    null comment '每多少人作为一段',
    private Integer commonWinMaxmum;  //          int default 30    null comment '普通奖上限值',
    private Integer commonWinMinmum;  //          int default 20    null comment '普通奖下限值',
    private Integer bodyPrizePosS;   //          int default 10    null comment '每段开始奖励的位置',
    private Integer bodyPrizePosE;   //          int default 20    null comment '每段结束奖励的位置',
    private Integer newUserWinAwards;    //      int default 2     null comment '1不区别新老用户，2新用户永远得大奖',
    private Integer personalCoinsLimit;   //      int               null comment '个人奖励上限值(不限于每次活动)',

    private Integer drawDays;              //      int default 1     null comment '用户使能参考天数',
    private Integer drawTimes;             //      int default 1     null comment '用户使能参考数量',

    private Integer realyJackpot;          //      int default 4000  null comment '奖池的值',
    private Integer visualJackpot;         //      int default 10000 null comment '虚拟显示奖池的值',
    private Integer expectedPersonNum;    //      int default 20    null comment '预期人数',
    private Integer expireTimeLong;       //      int default 1     null comment '过期时间长',
    private Integer expireTimeUnit;       //      int default 3     null comment '1天，2星期，3月',
    private Integer timeMode;              //      int default 1     null comment '1每天开启，2范围内一直开启',
    private Date openingPmt;               //      int               null comment '开始ymdhms，每天开始hms',
    private Date closingPmt;               //      int               null comment '结束ymdhms,每天结束hms'

}
