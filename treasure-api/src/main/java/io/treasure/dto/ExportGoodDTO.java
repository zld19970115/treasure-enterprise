package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Data
@ApiModel(value = "商品导入表")
public class ExportGoodDTO implements Serializable {
    private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "菜品名称")
	@NotBlank(message = "菜品名称不能为空",groups=AddGroup.class)
	private String name;
	@ApiModelProperty(value = "菜品描述")
	private String description;
	@ApiModelProperty(value = "菜品条码")
	private String barcode;
	@ApiModelProperty(value = "菜品原价格")
	@NotNull(message = "菜品原价不能为空",groups=AddGroup.class)
	private BigDecimal price;
	@ApiModelProperty(value = "菜品介绍")
	private String introduce;
	@ApiModelProperty(value = "是否热门 1=热门/0=默认")
	private Integer showInHot;
	@ApiModelProperty(value = "单位")
	private String units;
	@ApiModelProperty(value = "上架时间")
	private Date shelveTime;
	@ApiModelProperty(value = "上架人")
	private Long shelveBy;
	@ApiModelProperty(value = "商户")
	@NotNull(message = "商户不能为空",groups=AddGroup.class)
	private String martId;
	@ApiModelProperty(value = "菜品分类id")
	@NotNull(message = "分类不能为空",groups=AddGroup.class)
	private String goodCategoryId;
	@ApiModelProperty(value = "创建者")
	@NotNull(message = "创建者不能为空",groups = AddGroup.class)
	private Long creator;
	@ApiModelProperty(value = "创建时间")
	private Date createDate;
	@ApiModelProperty(value = "优惠价格")
	private BigDecimal favorablePrice;
	@ApiModelProperty(value = "状态 9-删除，1-启用，0-禁用")
	private Integer status;
}