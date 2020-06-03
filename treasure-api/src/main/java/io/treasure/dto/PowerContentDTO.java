package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    @ApiModelProperty(value = "助力人id")
    private Long userId;

    @ApiModelProperty(value = "助力人id")
    private Long powerUserId;

    @ApiModelProperty(value = "附加内容")
    private String subjoinContent;

    @ApiModelProperty(value = "助力类型（1-代付金助力）")
    private int powerType;



}
