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
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
@Data
@ApiModel(value = "包房或者桌表")
public class MerchantRoomDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "包房或者桌名称")
	@NotBlank(message ="名称不能为空",groups= AddGroup.class)
	@NotBlank(message = "名称不能为空",groups= UpdateGroup.class)
	private String name;

	@ApiModelProperty(value = "描述")
	private String description;

	@ApiModelProperty(value = "简介")
	private String brief;

	@ApiModelProperty(value = "图片")
	@NotBlank(message = "图片不能为空",groups=AddGroup.class)
	@NotBlank(message = "图片不能为空",groups=UpdateGroup.class)
	private String icon;

	@ApiModelProperty(value = "可容纳最低人数")
	@NotNull(message = "可容纳最低人数不能为空",groups=AddGroup.class)
	@NotNull(message = "可容纳最低人数不能为空",groups=UpdateGroup.class)
	private Integer numLow;

	@ApiModelProperty(value = "可容纳最高人数")
	@NotNull(message = "可容纳最高人数不能为空",groups=AddGroup.class)
	@NotNull(message = "可容纳最高人数不能为空",groups=UpdateGroup.class)
	private Integer numHigh;

	@ApiModelProperty(value = "类型，1-包房，2-桌")
	@NotNull(message = "类型不能为空",groups=AddGroup.class)
	@NotNull(message = "类型不能为空",groups=UpdateGroup.class)
	private Integer type;

	@ApiModelProperty(value = "商户")
	@NotNull(message = "商户不能为空",groups=AddGroup.class)
	@NotNull(message = "商户不能为空",groups=UpdateGroup.class)
	private Long merchantId;

	@ApiModelProperty(value = "状态")
	private String status;

	@ApiModelProperty(value = "修改时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	@NotNull(message = "创建者不能为空",groups=AddGroup.class)
	private Long creator;

	@ApiModelProperty(value = "修改者")
	@NotNull(message = "修改者不能为空",groups=UpdateGroup.class)
	private Long updater;


}