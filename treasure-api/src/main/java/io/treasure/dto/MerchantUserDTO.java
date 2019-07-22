package io.treasure.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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
	@NotBlank(message="手机号码不能为空")
	private String mobile;

	@ApiModelProperty(value = "密码")
	@NotBlank(message ="密码不能为空")
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
	private Long creator;

	@ApiModelProperty(value = "修改者")
	private Long updater;

	@ApiModelProperty(value = "商户编号")
	private Long merchantid;


}