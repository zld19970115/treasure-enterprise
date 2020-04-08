package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值现金设置表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Data
@ApiModel(value = "充值现金设置表")
public class ChargeCashSetDTO {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private Long id;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "类型")
    private Integer type;
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
    @ApiModelProperty(value = "增送比例")
    private String ratio;
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
