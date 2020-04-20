package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("查询用户订单量排行DTO")
public class ConsumptionRankingDto {

    @ApiModelProperty(value="开始日期")
    private String startDate;

    @ApiModelProperty("结束日期")
    private String endDate;

    @ApiModelProperty(value = "商户ID",required = true)
    private Long merchantId;

    @ApiModelProperty("排序字段 0总单数 1完成单数总价格 2完成单数 3取消单数总价格 4取消单数 默认0")
    private Integer sortField;

    @ApiModelProperty("排序 0正序 1倒序 默认1")
    private Integer sort;

}
