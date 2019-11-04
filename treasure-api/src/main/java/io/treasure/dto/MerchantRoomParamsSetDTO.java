package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


/**
 * 商户端包房设置管理
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
@Data
@ApiModel(value = "商户端包房设置管理")
public class MerchantRoomParamsSetDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "参数编号")
	private Long roomParamsId;
	@ApiModelProperty(value = "参数时间段")
	private String roomContent;
	@ApiModelProperty(value = "包房编号")
	@NotNull(message = "包房编号不能为空",groups = AddGroup.class)
	@NotNull(message = "包房编号不能为空",groups = UpdateGroup.class)
	private Long roomId;
	@ApiModelProperty(value = "包房名称")
	private String roomName;
	@ApiModelProperty(value = "1-包房，2-桌")
	private Integer type;

	@ApiModelProperty(value = "商户编号")
	@NotNull(message = "商户编号不能为空",groups = AddGroup.class)
	@NotNull(message = "商户编号不能为空",groups = UpdateGroup.class)
	private Long merchantId;

	@ApiModelProperty(value = "使用状态，0未使用,1已使用")
	private Integer state;

	@ApiModelProperty(value = "状态，9-删除，1-显示，0-隐藏")
	private Integer status;


	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "使用时间")
	private Date useDate;

	@ApiModelProperty(value = "修改时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	@NotNull(message = "创建者不能为空",groups = AddGroup.class)
	private Long creator;

	@ApiModelProperty(value = "修改者")
	@NotNull(message = "修改者不能为空",groups = UpdateGroup.class)
	private Long updater;

	@ApiModelProperty(value = "包房或者桌名称")
	private String name;

	@ApiModelProperty(value = "图片")
	private String icon;

	@ApiModelProperty(value = "状态")
	private Integer crstatus;

	@ApiModelProperty(value = "可容纳最低人数")
	private Integer numlow;

	@ApiModelProperty(value = "可容纳最高人数")
	private Integer numhigh;

	@ApiModelProperty(value = "店铺名称")
	private String Dname;

	@ApiModelProperty(value = "已使用总数")
	private Integer Allaleady;

	@ApiModelProperty(value = "未使用总数")
	private Integer AllNOT;
}