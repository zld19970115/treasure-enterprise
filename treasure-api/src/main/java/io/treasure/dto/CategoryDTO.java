package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Data
@ApiModel(value = "店铺类型分类表")
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "分类ID")
	private Long id;

	@ApiModelProperty(value = "上级分类ID")
	private Long pid;

	@ApiModelProperty(value = "分类名称")
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

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "更新者")
	private Long updater;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;


}