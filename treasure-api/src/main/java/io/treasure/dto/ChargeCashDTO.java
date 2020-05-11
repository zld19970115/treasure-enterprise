package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值现金表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Data
@ApiModel(value = "充值现金表")
public class ChargeCashDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private Long id;
    @ApiModelProperty(value = "类型")
    private String cashOrderId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private long userId;
    /**
     * 存入现金金额
     */
    @ApiModelProperty(value = "存入现金金额")
    private BigDecimal cash;
    /**
     * 赠送代付金金额
     */
    @ApiModelProperty(value = "赠送代付金金额")
    private BigDecimal changeGift;
    /**
     * 存入时间
     */
    @ApiModelProperty(value = "存入时间")
    private Date saveTime;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
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
}
