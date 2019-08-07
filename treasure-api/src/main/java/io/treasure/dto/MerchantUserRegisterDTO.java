package io.treasure.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;


/**
 * 商户管管理员注册
 *王丽娜
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-22
 */
@Data
@ApiModel(value = "商户管理员注册")
public class MerchantUserRegisterDTO {
	@ApiModelProperty(value = "手机号码")
	@NotBlank(message="手机号码不能为空")
	private String mobile;
	@ApiModelProperty(value = "密码")
	@NotBlank(message ="密码不能为空")
	private String oldPassword;
	@ApiModelProperty(value = "密码")
	@NotBlank(message ="确认密码不能为空")
	private String newPassword;
	@ApiModelProperty(value = "微信名称")
	private String weixinname;
	@ApiModelProperty(value = "微信头像")
	private String weixinurl;
	@ApiModelProperty(value = "")
	private String openid;
}