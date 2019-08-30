package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@ApiModel(value = "用户优惠卷表")
public class UserCouponDTO implements Serializable
{
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private long Id;
    /**
     * .用户id
     */
    @ApiModelProperty(value = "用户id")
    private  long userId;
    /**
     * 用户卷id
     */
    @ApiModelProperty(value = "用户卷id")
    private long couponId;

    /**
    /**
     * 商家id
     */
    @ApiModelProperty(value = "商家id")
    private long martId;
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
}
