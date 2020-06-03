package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

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

    @ApiModelProperty(value = "助力级别（1-一级助力，2-二级助力）")
    private String powerlevel;

    @ApiModelProperty(value = "助力获得的代付金")
    private BigDecimal powerGift;

    @ApiModelProperty(value = "助力人数")
    private int powerSum;





}
