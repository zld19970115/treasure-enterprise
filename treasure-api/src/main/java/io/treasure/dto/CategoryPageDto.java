package io.treasure.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CategoryPageDto {

    @ApiModelProperty(value = "分类ID")
    private Long id;

    @ApiModelProperty(value = "上级分类")
    private String pidName;

    @ApiModelProperty(value = "分类")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态 1=显示/0=隐藏")
    private Integer status;

    @ApiModelProperty(value = "是否推荐（0：不推荐 1：推荐）")
    private Integer showInCommend;

    @ApiModelProperty(value = "是否导航栏 1：显示  0：隐藏")
    private Integer showInNav;

    @ApiModelProperty(value = "分类小图标")
    private String icon;

    @ApiModelProperty(value = "备注信息")
    private String remarks;

    @ApiModelProperty(value = "创建时间")
    private String createDate;

}
