package io.treasure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("business_manager")
public class BusinessManagerEntity {

    @TableId(value = "id",type = IdType.INPUT)
    private Integer id;
    private String mobile;
    private String nick_name;   //char(20)                            null,
    private String password;    //char(128)                           not null,
    private Date contract_expire;   //datetime                            null,
    private String icon;        //char(200)                           null comment '头像',
    private String position;    //char(10)                            null comment '职位',
    private String real_name;   //char(10)                            not null,
    private String uid;         //char(20)                            not null comment '身份证号',
    private Date hiredate;      //timestamp default CURRENT_TIMESTAMP null comment '入职时间',
    private Integer star;       //int(10)   default 10000             null comment '评级',
    private String emergent_contact;//char(11)                            null comment '紧急联系人',
    private Integer wage_base;  //int(10)   default 0                 null comment '工资基数：单位分',
    private Integer status;     //int(2)                              null comment '状态1:试用，2：正式入职，3:离

}
