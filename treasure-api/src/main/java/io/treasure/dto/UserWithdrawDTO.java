package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
@Data
@ApiModel(value = "用户提现表")
public class UserWithdrawDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "商户名称")
    private String name;
    @ApiModelProperty(value = "提现金额")
    @NotNull(message="提现金额不能为空",groups = AddGroup.class)
    private Double money;

    @ApiModelProperty(value = "类型:1-微信，2-支付宝，3-银行卡")
    private Integer type;

    @ApiModelProperty(value = "方式：1-自动提现，2-手动提现")
    private Integer way;

    @ApiModelProperty(value = "用户")
    @NotNull(message="用户不能为空",groups = AddGroup.class)
    private Long userId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "修改时间")
    private Date updateDate;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "创建者")
    @NotNull(message="创建者不能为空",groups = AddGroup.class)
    private Long creator;
    @ApiModelProperty(value = "审核状态")
    private Integer verifyState;
    @ApiModelProperty(value = "修改者")
    private Long updater;

    @ApiModelProperty(value = "审核人")
    private Long verify;

    @ApiModelProperty(value = "审核时间")
    private Date verifyDate;

    @ApiModelProperty(value = "审核意见")
    private String verifyReason;
    @ApiModelProperty(value = "会员姓名")
    private String userName;
    @ApiModelProperty(value = "会员联系电话")
    private String userMobile;
    @ApiModelProperty(value = "会员昵称")
    private String nickName;
    @ApiModelProperty(value = "营业额总收入")
    private String totalCash;
    @ApiModelProperty(value = "会员手机号码")
    private String mobile;

    @ApiModelProperty(value = "用户信息")
    private ClientUserDTO clientUserDTO;
}
