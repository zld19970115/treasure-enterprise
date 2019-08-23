package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


/**
 * 商户广告位推广
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@ApiModel(value = "商户广告位推广")
public class MerchantAdvertExtendDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "广告图片")
	@NotBlank(message ="广告图片不能为空",groups= AddGroup.class)
	@NotBlank(message = "广告不能为空",groups= UpdateGroup.class)
	private String url;

	@ApiModelProperty(value = "广告链接")
	@NotBlank(message ="广告链接不能为空",groups= AddGroup.class)
	@NotBlank(message = "广告链接不能为空",groups= UpdateGroup.class)
	private String link;

	@ApiModelProperty(value = "商户")
	@NotNull(message ="商户不能为空",groups= AddGroup.class)
	@NotNull(message = "商户不能为空",groups= UpdateGroup.class)
	private Long merchantId;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "状态")
	private Integer status;

	@ApiModelProperty(value = "修改时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	@NotNull(message ="创建者不能为空",groups= AddGroup.class)
	private Long creator;

	@ApiModelProperty(value = "修改者")
	@NotNull(message = "修改者不能为空",groups= UpdateGroup.class)
	private Long updater;


}