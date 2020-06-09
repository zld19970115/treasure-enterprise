package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author user
 * @title: 助力级别
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:11
 */
@Data
@ApiModel(value = "助力级别表")
public class PowerLevelDTO {

    @ApiModelProperty(value = "助力id")
    private int powerlevelId;

    @ApiModelProperty(value = "用户id")
    private long userId;

    @ApiModelProperty(value = "助力奖励类别（1-代付金，2-商品，3-菜品）")
    private String powerType;

    @ApiModelProperty(value = "获得助力奖励次数")
    private int powerGain;

    @ApiModelProperty(value = "助力人数")
    private int powerSum;

    @ApiModelProperty(value = "助力状态（1-助力完成，0-助力中）")
    private int powerFinish;

    @ApiModelProperty(value = "助力完成时间")
    private Date powerAbortDate;

    @ApiModelProperty(value = "助力开始时间")
    private Date powerOpenDate;

    @ApiModelProperty(value = "助力随机数")
    private String ramdomNumber;




}
