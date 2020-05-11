package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "商家账户详情VO")
public class MerchantAccountVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "手机号")
    private BigDecimal mobile;

    @ApiModelProperty(value = "营业总收入")
    private BigDecimal totalCash;

    @ApiModelProperty(value = "商家已提现金额")
    private BigDecimal alreadyCash;

    @ApiModelProperty(value = "可提现金额")
    private BigDecimal notCash;

    @ApiModelProperty(value = "审核中金额")
    private BigDecimal wartCash;

    @ApiModelProperty(value = "扣点金额")
    private BigDecimal pointMoney;

    @ApiModelProperty(value = "销量")
    private Integer count;

    @ApiModelProperty(value = "微信总提现金额")
    private BigDecimal wxTotal;

    @ApiModelProperty(value = "支付宝总提现金额")
    private BigDecimal aliTotald;

}
