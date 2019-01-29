package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
	private Integer id;

	@ApiModelProperty(value = "菜品名称")
	private String name;

	@ApiModelProperty(value = "菜品描述")
	private String description;

	@ApiModelProperty(value = "菜品条码")
	private String barcode;

	@ApiModelProperty(value = "菜品原价格")
	private BigDecimal price;

	@ApiModelProperty(value = "菜品介绍")
	private String introduce;

	@ApiModelProperty(value = "是否热门 1=热门/0=默认")
	private Integer showInHot;

	@ApiModelProperty(value = "菜品关键词")
	private String key;

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
	private String offShelveCause;

	@ApiModelProperty(value = "商户id")
	private Long martId;

	@ApiModelProperty(value = "菜品分类id")
	private Long goodCategoryId;

	@ApiModelProperty(value = "销售量")
	private Integer sales;

	@ApiModelProperty(value = "购买人数")
	private Integer buyers;

	@ApiModelProperty(value = "状态(0:禁用，1：使用,9:记录删除)")
	private Integer status;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "更新者")
	private Long updater;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	@ApiModelProperty(value = "图片")
	private String icon;

	@ApiModelProperty(value = "优惠价格")
	private BigDecimal favorablePrice;

	@ApiModelProperty(value = "库存量")
	private Integer stock;


}