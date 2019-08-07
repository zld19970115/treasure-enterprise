package io.treasure.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * 商户管理员
 *王丽娜
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
@Data
@ApiModel(value = "商户管理员")
public class MerchantUserDTO {

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "手机号码")
	@NotBlank(message="手机号码不能为空",groups = AddGroup.class)
	@NotBlank(message="手机号码不能为空",groups = UpdateGroup.class)
	private String mobile;

	@ApiModelProperty(value = "密码")
	@NotBlank(message ="密码不能为空",groups = AddGroup.class)
	private String password;

	@ApiModelProperty(value = "微信名称")
	private String weixinname;

	@ApiModelProperty(value = "微信头像")
	private String weixinurl;

	@ApiModelProperty(value = "")
	private String openid;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "状态")
	private Integer status;

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
	@ApiModelProperty(value = "商户编号")
	@NotBlank(message = "商户不能为空",groups=AddGroup.class)
	@NotBlank(message = "商户不能为空",groups = UpdateGroup.class)
	private String merchantid;
	@ApiModelProperty(value = "父节点")
	@NotNull(message = "父节点不能为空",groups = AddGroup.class)
	@NotNull(message = "父级点不能为空",groups = UpdateGroup.class)
	private Long pid;
	@NotNull(message = "角色不能为空",groups=AddGroup.class)
	@NotNull(message = "角色不能为空",groups = UpdateGroup.class)
	private Long roleId;

}