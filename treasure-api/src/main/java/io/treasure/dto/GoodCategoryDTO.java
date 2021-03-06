package io.treasure.dto;

import io.swagger.annotations.Api;
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
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Data
@ApiModel(value = "店铺类型分类表")
public class GoodCategoryDTO implements Serializable {
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
	private String icon;
	@ApiModelProperty(value = "分类简介")
	private String brief;
	@ApiModelProperty(value = "创建者")
	@NotNull(message = "创建者不能为空",groups = AddGroup.class)
	private Long creator;
	@ApiModelProperty(value="修改者")
	@NotNull(message = "修改者不能为空",groups = UpdateGroup.class)
	private Long updater;
	@ApiModelProperty(value="商户")
	@NotNull(message = "商户不能为空",groups =AddGroup.class)
	@NotNull(message = "商户不能为空",groups = UpdateGroup.class)
	private Long merchantId;
	@ApiModelProperty(value="创建时间")
	private Date createDate;
	@ApiModelProperty(value="店铺名称")
	private String merchantName;

	@ApiModelProperty(value="销量")
	private BigDecimal alquantity;
	@ApiModelProperty(value="交易金额")
	private BigDecimal allPayMoney;
	@ApiModelProperty(value="可提现金额")
	private BigDecimal subtract;
	@ApiModelProperty(value="平台服务费")
	private BigDecimal multiply;
}