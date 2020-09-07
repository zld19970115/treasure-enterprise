package io.treasure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Data
@ApiModel(value = "用户信息")
@Accessors(chain = true)
public class ClientUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "姓名")
	private String name;

	@ApiModelProperty(value = "昵称")
	private String username;

	@ApiModelProperty(value = "身份证号码")
	private String idCard;

	@ApiModelProperty(value = "身份证正面照")
	private String idcardFrontImg;

	@ApiModelProperty(value = "身份证反面照")
	private String idcardBackImg;

	@ApiModelProperty(value = "性别 1-男 2-女")
	private String sex;

	@ApiModelProperty(value = "年龄")
	private Integer age;

	@ApiModelProperty(value = "出生日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date birth;

	@ApiModelProperty(value = "密码")
	//@NotEmpty(message = "password is not null")
	private String password;

	@ApiModelProperty(value = "手机号")
	@NotEmpty(message = "mobile is not null")
	private String mobile;

	@ApiModelProperty(value = "头像图片地址")
	private String headImg;

	@ApiModelProperty(value = "积分")
	private BigDecimal integral;

	@ApiModelProperty(value = "余额")
	private BigDecimal balance;

	@ApiModelProperty(value = "金币")
	private BigDecimal coin;

	@ApiModelProperty(value = "赠送金")
	private BigDecimal gift;

	@ApiModelProperty(value = "用户级别")
	private Integer level;

	@ApiModelProperty(value = "微信用户唯一标识")
	private String openid;

	@ApiModelProperty(value = "微信unionid")
	private String unionid;

	@ApiModelProperty(value = "状态 0=冻结/1=正常/9=删除")
	private Integer status;

	@ApiModelProperty(value = "注册来源 --1点餐APP 2店铺APP 3后台")
	private String origin;

	@ApiModelProperty(value = "注册方式1手机号 2微信号")
	private String way;
	@ApiModelProperty(value = "收款支付宝账户")
	private String aliAccountNumber;
	/**
	 * 支付宝收款人真实姓名
	 */
	@ApiModelProperty(value = "支付宝收款人真实姓名")
	private String aliAccountRealname;
	/**
	 * 收款微信openid
	 */
	@ApiModelProperty(value = "收款微信openid")
	private String wxAccountOpenid;

	@ApiModelProperty(value = " 微信绑定方式 1---小程序 2---app")
	private Integer wxStatus;
	@ApiModelProperty(value = "更新时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;

	@ApiModelProperty(value = "创建时间")
	private String createTime;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "更新者")
	private Long updater;
	@ApiModelProperty(value = "个推客户Id")
	private String clientId;

	@ApiModelProperty(value = "限时宝币")
	private BigDecimal display;

	public void setGift(BigDecimal gift){
		System.out.println("the gift current value:"+gift);
		this.gift = gift;
	}

}