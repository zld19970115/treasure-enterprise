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
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
@Data
@ApiModel(value = "商户活动管理")
public class MerchantActivityDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@NotNull(message ="编号不能为空",groups = UpdateGroup.class)
	private Long id;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "活动内容")
	@NotBlank(message = "活动内容不能为空",groups = AddGroup.class)
	@NotBlank(message = "活动内容不能为空",groups = UpdateGroup.class)
	private String content;

	@ApiModelProperty(value = "简介")
	private String brief;

	@ApiModelProperty(value = "图片")
	@NotBlank(message = "图片不能为空",groups = AddGroup.class)
	@NotBlank(message = "图片不能为空",groups = UpdateGroup.class)
	private String icon;

	@ApiModelProperty(value = "类型")
	private Integer type;

	@ApiModelProperty(value = "商户")
	@NotNull(message = "商户不能为空",groups = AddGroup.class)
	@NotNull(message = "商户不能为空",groups = UpdateGroup.class)
	private Long merchantId;

	@ApiModelProperty(value = "")
	@NotNull(message = "创建者不能为空",groups = AddGroup.class)
	private Long creator;

	@ApiModelProperty(value = "")
	@NotNull(message = "修改者不能为空",groups = UpdateGroup.class)
	private Long updater;
	private Integer status;

}