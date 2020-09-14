package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("coins_activities")
public class CoinsActivitiesEntity {

    @TableId
    private Long id;                    //          bigint auto_increment
    private Integer type;               //          int default 0     null comment '1宝币活动',
    private Integer status;             //          int default 1     null comment '1有效，2无效',
    private Integer mode;               //          int default 1     null comment '1常规，2忽略人数',
    private Integer head_person_num;    //          int default 10    null comment '首段人数',
    private Integer first_prize_num;    //          int default 1     null comment '头奖数',
    private Integer first_prize_pos_s;  //          int default 0     null comment '头奖出现的开始位置（包含）',
    private Integer first_prize_pos_e;  //          int default 0     null comment '头奖出现的结束位置（包含）',
    private Integer prize_maxmum;       //          int default 50    null comment '头奖高限',
    private Integer prize_minmun;       //          int default 30    null comment '头奖低限',
    private Integer body_segment_num;   //          int default 10    null comment '每多少人作为一段',
    private Integer common_win_maxmum;  //          int default 30    null comment '普通奖上限值',
    private Integer common_win_minmum;  //          int default 20    null comment '普通奖下限值',
    private Integer body_prize_pos_s;   //          int default 10    null comment '每段开始奖励的位置',
    private Integer body_prize_pos_e;   //          int default 20    null comment '每段结束奖励的位置',
    private Integer new_user_win_awards;    //      int default 2     null comment '1不区别新老用户，2新用户永远得大奖',
    private Integer personal_coins_limit;   //      int               null comment '个人奖励上限值(不限于每次活动)',
    private Integer draw_days;              //      int default 1     null comment '用户使能参考天数',
    private Integer draw_times;             //      int default 1     null comment '用户使能参考数量',
    private Integer realy_jackpot;          //      int default 4000  null comment '奖池的值',
    private Integer visual_jackpot;         //      int default 10000 null comment '虚拟显示奖池的值',
    private Integer expected_person_num;    //      int default 20    null comment '预期人数',
    private Integer expire_time_long;       //      int default 1     null comment '过期时间长',
    private Integer expire_time_unit;       //      int default 3     null comment '1天，2星期，3月',
    private Integer time_mode;              //      int default 1     null comment '1每天开启，2范围内一直开启',
    private Date opening_pmt;               //      int               null comment '开始ymdhms，每天开始hms',
    private Date closing_pmt;               //      int               null comment '结束ymdhms,每天结束hms'

}
