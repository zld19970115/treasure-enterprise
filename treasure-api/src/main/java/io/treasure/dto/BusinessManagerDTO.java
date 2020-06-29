package io.treasure.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "业务员表")
public class BusinessManagerDTO  implements Serializable {
    @ApiModelProperty(value = "ID")
    private Long id;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "密码")//char(20)                            null,
    private String password;
    @ApiModelProperty(value = "合约到期日")//char(128)                           not null,
    private Date contractExpire;
    @ApiModelProperty(value = "头像")//datetime                            null,
    private String icon;
    @ApiModelProperty(value = "职位")//char(200)                           null comment '头像',
    private String position;
    @ApiModelProperty(value = "真实姓名")//char(10)                            null comment '职位',
    private String realName;
    @ApiModelProperty(value = "身份证号")//char(10)                            not null,
    private String uid;
    @ApiModelProperty(value = "入职时间")//char(20)                            not null comment '身份证号',
    private Date hiredate;
    @ApiModelProperty(value = "评级")//timestamp default CURRENT_TIMESTAMP null comment '入职时间',
    private Integer star;
    @ApiModelProperty(value = "紧急联系人")//int(10)   default 10000             null comment '评级',
    private String emergentContact;
    @ApiModelProperty(value = "工资基数：单位分")//char(11)                            null comment '紧急联系人',
    private Integer wageBase;
    @ApiModelProperty(value = "状态1:试用，2：正式入职，3:离职")//int(10)   default 0                 null comment '工资基数：单位分',
    private Integer status;     //int(2)                              null comment '状态1:试用，2：正式入职，3:离
    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Integer deleted;
}
