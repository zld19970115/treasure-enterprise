package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author user
 * @title: 助力信息
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/215:11
 */
@Data
@ApiModel(value = "助力信息表")
public class PowerContentDTO {

    @ApiModelProperty(value = "助力id")
    private int powerlevelId;

    @ApiModelProperty(value = "助力活动名称")
    private String powerName;

    @ApiModelProperty(value = "助力奖励类别（1-代付金，2-商品，3-菜品）")
    private int powerType;

    @ApiModelProperty(value = "助力奖励数量")
    private int powerSum;

    @ApiModelProperty(value = "助力类型")
    private int powerPeopleSum;

    @ApiModelProperty(value = "活动内容")
    private String subjoinContent;

    @ApiModelProperty(value = "菜品id")
    private Long goodId;

    @ApiModelProperty(value = "商品id")
    private Long merchandiseId;

    @ApiModelProperty(value = "活动开始时间")
    private Date activityOpenDate;

    @ApiModelProperty(value = "活动截止日期")
    private Date activityAbortDate;


}
