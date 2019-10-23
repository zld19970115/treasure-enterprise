package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Data
@ApiModel(value = "商户端优惠卷")
public class MerchantCouponDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private long id;

	@ApiModelProperty(value = "名称")
	@NotBlank(message ="名称不能为空",groups= AddGroup.class)
	@NotBlank(message = "名称不能为空",groups= UpdateGroup.class)
	private String name;

	@ApiModelProperty(value = "类型1-满减卷，2-满赠卷，3-优惠卷")
	@NotNull(message ="类型不能为空",groups= AddGroup.class)
	@NotNull(message = "类型不能为空",groups= UpdateGroup.class)
	private Integer type;

	@ApiModelProperty(value = "满多少金额可以使用优惠卷")
	private Double money;

	@ApiModelProperty(value = "优惠类型：1-金额；2-折扣")
	private Integer disType;

	@ApiModelProperty(value = "优惠折扣")
	private Double discount;

	@ApiModelProperty(value = "开始时间")
	@NotNull(message ="开始时间不能为空",groups= AddGroup.class)
	@NotNull(message = "开始时间不能为空",groups= UpdateGroup.class)
	private String startDate;

	@ApiModelProperty(value = "结束时间")
	@NotNull(message ="结束时间不能为空",groups= AddGroup.class)
	@NotNull(message = "结束不能为空",groups= UpdateGroup.class)
	private String endDate;

	@ApiModelProperty(value = "商户")
	@NotNull(message ="商户不能为空",groups= AddGroup.class)
	@NotNull(message = "商户不能为空",groups= UpdateGroup.class)
	private Long merchantId;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "9-删除，1-显示，0-隐藏")
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

	@ApiModelProperty(value = "1-可重复领取，2-不可重复领取")
	@NotNull(message ="是否重复领取不能为空",groups= AddGroup.class)
	@NotNull(message = "是否重复领取不能为空",groups= UpdateGroup.class)
	private Integer isRepeat;

	@ApiModelProperty(value = "发放条件:1-自己领取,2-满额自动领取，3-自动发放")
	private Integer grants;

	@ApiModelProperty(value = "商户名称")
	private String merchantName;

}