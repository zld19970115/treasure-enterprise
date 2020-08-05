package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("client_member_grade_assessment")
public class ClientMemberGradeAssessmentEntity {

    @TableId
    private Integer id;                     //
    private Integer baseLevel;              //       int(6)   null comment '默认的级别,新注册为1级',
    private Integer twoStar;                //          int(6)   null comment '二星',
    private Integer threeStar;              //        int(6)   null comment '三星',
    private Integer fourStar;               //         int(6)   null comment '四星',
    private Integer fiveStar;               //         int(6)   null comment '五星',
    private Integer calculateMethod;        //  int      null comment '计算方式1充值总额与实付款总额',
    private Integer timeRange;              //        int(2)   null comment '评定范围(自本月开始至上n-1个月内)',
    private Integer intervalTime;           //     int      null comment '评定时间(每n个月)',
    private Date assessmentTime;            //   datetime null comment '评定时间',
    private Long modify;                    //            bigint   null comment '修改人'

}
