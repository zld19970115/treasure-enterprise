package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户赠送金表
 * 2019.9.3
 */
@Data
@ApiModel(value = "用户赠送金表")
public class UserGiftDTO {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private long id;
    /**
     * 卡劵账号
     */
    @ApiModelProperty(value = "卡劵账号")
    private String number;
    /**
     * 密碼
     */
    @ApiModelProperty(value = "密碼")
    private Integer password;
    /**
     * 用戶賬號（手机号）
     */
    @ApiModelProperty(value = "用戶賬號（手机号）")
    private String userNumber;
    /**
     * 状态 0--未使用 1--已使用
     */
    @ApiModelProperty(value = "状态 0--未使用 1--已使用")
    private Integer status;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    @NotNull(message = "更新者不能为空",groups = AddGroup.class)
    private long creator;
    /**
     * 修改者
     */
    @ApiModelProperty(value = "更新者")
    @NotNull(message = "更新者不能为空",groups = UpdateGroup.class)
    private long updater;
    /**
     * 面额
     */
    @ApiModelProperty(value = "面额")
    private BigDecimal money;
    @ApiModelProperty(value = "截止日期")
    private Date  endDate;
}
