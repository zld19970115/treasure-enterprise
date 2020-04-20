package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("查询商户热销产品排行统计DTO")
public class TopSellersRankingDto {

    @ApiModelProperty(value="开始日期")
    private String startDate;

    @ApiModelProperty("结束日期")
    private String endDate;

    @ApiModelProperty(value = "商户ID",required = true)
    private Long merchantId;

    @ApiModelProperty("排序字段 0总金额 1总数量 默认0")
    private Integer sortField;

    @ApiModelProperty("排序 0正序 1倒序 默认1")
    private Integer sort;

}
