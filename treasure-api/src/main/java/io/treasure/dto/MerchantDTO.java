package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
@Data
@ApiModel(value = "商户表")
public class MerchantDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "手机号码")
	private String mobile;

	@ApiModelProperty(value = "商户名称")
	@NotBlank(message = "商户名称不能为空")
	private String name;

	@ApiModelProperty(value = "商户编码")
	private String idcode;

	@ApiModelProperty(value = "商户地址")
	@NotBlank(message = "商户地址不能为空")
	private String address;

	@ApiModelProperty(value = "商户简介")
	private String brief;

	@ApiModelProperty(value = "营业时间")
	@NotBlank(message = "营业时间不能为空")
	private String businesshours;

	@ApiModelProperty(value = "经营类别")
	@NotNull(message = "经营类别不能为空")
	private Long categoryid;

	@ApiModelProperty(value = "头像")
	@NotBlank(message = "头像不能为空")
	private String headurl;

	@ApiModelProperty(value = "评分")
	private Double score;

	@ApiModelProperty(value = "经度")
	@NotBlank(message = "精度不能为空")
	private String log;

	@ApiModelProperty(value = "纬度")
	@NotBlank(message = "纬度不能为空")
	private String lat;

	@ApiModelProperty(value = "营业执照")
	@NotBlank(message = "营业执照不能为空")
	private String businesslicense;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "状态 0=冻结,1=正常")
	private Integer status;

	@ApiModelProperty(value = "修改时间")
	private Date updateDate;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "修改者")
	private Long updater;

	@ApiModelProperty(value = "身份证号")
	@NotBlank(message = "身份证号不能为空")
	private String cards;

	@ApiModelProperty(value = "1-未审核，2-审核通过，3-审核未通过;审核状态")
	private Integer auditstatus;
	@ApiModelProperty(value = "闭店时间")
	@NotBlank(message = "闭店时间不能为空")
	private String closeshophours;
	@ApiModelProperty(value ="联系电话")
	private String tel;
	@ApiModelProperty(value ="预约天数")
	private Integer days;
	@ApiModelProperty(value ="是否推荐：0否，1是；")
	private String recommend;
	@ApiModelProperty(value ="特色菜；多个菜名用英文逗号分隔")
	private String characteristic;
	@ApiModelProperty(value ="距离（米）")
	private String distance;



}