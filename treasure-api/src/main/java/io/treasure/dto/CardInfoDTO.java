package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "充值赠送金表")
public class CardInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private long id;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 面值
     */
    @ApiModelProperty(value = "面值")
    private BigDecimal money;

    /**
     * P批次
     */
    @ApiModelProperty(value = "批次")
    private Integer batch;

    /**
     * 类型：1-赠送金
     */
    @ApiModelProperty(value = "类型")
    private String type;
    /**
     * 卡状态：1-制卡，2-开卡，3-绑定卡，9-删除
     */
    @ApiModelProperty(value = "卡状态")
    private Integer status;
    /**
     * 开卡时间
     */
    @ApiModelProperty(value = "开卡时间")
    private Date openCardDate;
    /**
     * 开卡人
     */
    @ApiModelProperty(value = "开卡人")
    private Date openCardUser;
    /**
     * 绑定时间
     */
    @ApiModelProperty(value = "绑定时间")
    private Date bindCardDate;
    /**
     * 绑定人
     */
    @ApiModelProperty(value = "绑定人")
    private Date bindCardUser;

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
}
