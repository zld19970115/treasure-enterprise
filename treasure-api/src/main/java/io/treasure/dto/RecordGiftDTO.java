package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "赠送金记录表")
public class RecordGiftDTO  implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 记录ID
     */
    @ApiModelProperty(value = "记录ID")
    private long id;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private long userId;
    /**
     * 被转移用户手机号
     */
    @ApiModelProperty(value = "被转移用户手机号")
    private String transferredMobile;
    /**
     * 使用或充值得赠送金
     */
    @ApiModelProperty(value = "使用或充值得赠送金")
    private BigDecimal useGift;
    /**
     * 赠送金余额
     */
    @ApiModelProperty(value = "赠送金余额")
    private BigDecimal balanceGift;
    /**
     * 状态0---充值类型   1-----使用类型
     */
    @ApiModelProperty(value = "状态0---充值类型 1-----使用类型")
    private Integer status;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
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
    private long creator;
    /**
     * 修改者
     */
    @ApiModelProperty(value = "修改者")
    private long updater;
    /**
     * 修改者
     */
    @ApiModelProperty(value = "用户真实名")
    private long name;
    /**
     * 修改者
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    private String mobile;

}
