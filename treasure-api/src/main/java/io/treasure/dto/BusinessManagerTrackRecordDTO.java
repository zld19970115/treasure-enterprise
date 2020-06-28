package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "业务员招商记录表")
public class BusinessManagerTrackRecordDTO implements Serializable {

    @ApiModelProperty(value = "pid")
    private Integer pid;                // int(10) auto_increment
    @ApiModelProperty(value = "业务员Id")
    private Integer bmId;
    @ApiModelProperty(value = "商户id")// int(10) not null,业务员id
    private Long mchId;                 // bigint  not null,
    @ApiModelProperty(value = "入驻时间")
    private Date officialEntryTime;     // atetime default CURRENT_TIMESTAMP null comment '入驻时间',
    @ApiModelProperty(value = "符合商家条件时间")
    private Date becomeABusinessTime;
    @ApiModelProperty(value = "1:开始入驻，2审核完成，3营业状态")// datetime  null,
    private Integer status;             // int(2)   null comment '1:开始入驻，2审核完成，3营业状态'







}
