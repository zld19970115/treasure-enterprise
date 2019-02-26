package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;


/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Data
@ApiModel(value = "店铺类型分类表")
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "")
	private Long id;
	@ApiModelProperty(value = "分类名称")
	@NotBlank(message = "分类名称不能为空")
	private String name;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "状态 1=显示/0=隐藏")
	private Integer status;

	@ApiModelProperty(value = "是否推荐（0：不推荐 1：推荐）")
	private Integer showInCommend;

	@ApiModelProperty(value = "分类小图标")
	@NotBlank(message = "分类图片不能为空")
	private String icon;
	@ApiModelProperty(value = "分类简介")
	@NotBlank(message = "分类简介不能为空")
	private String brief;
	@ApiModelProperty(value = "创建者")
	private Long creator;
	@ApiModelProperty(value="商户")
	@NotBlank(message = "商户不能为空")
	private Long merchant_id;
}