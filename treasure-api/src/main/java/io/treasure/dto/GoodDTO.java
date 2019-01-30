package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;
import org.springframework.web.bind.annotation.PutMapping;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Data
@ApiModel(value = "商品表")
public class GoodDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "菜品ID")
	@NotNull(message = "菜品编号不能为空",groups = UpdateGroup.class)
	private Long id;
	@ApiModelProperty(value = "菜品名称")
	@NotBlank(message = "菜品名称不能为空",groups=AddGroup.class)
	@NotBlank(message = "菜品名称不能为空",groups=UpdateGroup.class)
	private String name;
	@ApiModelProperty(value = "菜品描述")
	private String description;
	@ApiModelProperty(value = "菜品条码")
	private String barcode;
	@ApiModelProperty(value = "菜品原价格")
	@NotNull(message = "菜品原价不能为空",groups=AddGroup.class)
	@NotNull(message = "菜品原价不能为空",groups=UpdateGroup.class)
	private BigDecimal price;
	@ApiModelProperty(value = "菜品介绍")
	@NotBlank(message="菜品介绍不能为空",groups=AddGroup.class)
	@NotBlank(message="菜品介绍不能为空",groups=UpdateGroup.class)
	private String introduce;
	@ApiModelProperty(value = "是否热门 1=热门/0=默认")
	private Integer showInHot;
	@ApiModelProperty(value = "菜品关键词")
	private String keys;
	@ApiModelProperty(value = "单位")
	private String units;
	@ApiModelProperty(value = "备注信息")
	private String remarks;
	@ApiModelProperty(value = "上架时间")
	private Date shelveTime;
	@ApiModelProperty(value = "上架人")
	private Long shelveBy;
	@ApiModelProperty(value = "下架时间")
	private Date offShelveTime;
	@ApiModelProperty(value = "下架人")
	private Long offShelveBy;
	@ApiModelProperty(value = "下架原因")
	private String offShelveReason;
	@ApiModelProperty(value = "商户id")
	@NotNull(message = "商户不能为空",groups=AddGroup.class)
	@NotNull(message = "商户不能为空",groups=UpdateGroup.class)
	private Long martId;

	@ApiModelProperty(value = "菜品分类id")
	@NotNull(message = "分类不能为空",groups=AddGroup.class)
	@NotNull(message = "分类不能为空",groups=UpdateGroup.class)
	private Long goodCategoryId;
	@ApiModelProperty(value = "销售量")
	private Integer sales;
	@ApiModelProperty(value = "购买人数")
	private Integer buyers;
	@ApiModelProperty(value = "创建者")
	@NotNull(message = "更新者不能为空",groups = AddGroup.class)
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;
	@ApiModelProperty(value = "更新者")
	@NotNull(message = "更新者不能为空",groups = UpdateGroup.class)
	private Long updater;
	@ApiModelProperty(value = "更新时间")
	private Date updateDate;
	@ApiModelProperty(value = "图片")
	@NotBlank(message = "图片不能为空",groups=AddGroup.class)
	@NotBlank(message = "图片不能为空",groups=UpdateGroup.class)
	private String icon;
	@ApiModelProperty(value = "优惠价格")
	@NotNull(message = "优惠价格不能为空",groups=AddGroup.class)
	@NotNull(message = "优惠价格不能为空",groups=UpdateGroup.class)
	private BigDecimal favorablePrice;
	@ApiModelProperty(value = "库存量")
	@NotNull(message = "库存量不能为空",groups=AddGroup.class)
	@NotNull(message = "库存量不能为空",groups=UpdateGroup.class)
	private Integer stock;
	@ApiModelProperty(value = "状态 9-删除，1-启用，0-禁用")
	@NotNull( message = "状态不能为空",groups=AddGroup.class)
	@NotNull(message="状态不能为空",groups=UpdateGroup.class)
	private Integer status;

}