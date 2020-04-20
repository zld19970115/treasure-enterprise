package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@ApiModel("查询商户热销产品排行统计VO")
public class TopSellersRankingVo {

    @ApiModelProperty(value="商品名称")
    private String name;

    @ApiModelProperty(value="商品数量")
    private Long count;

    @ApiModelProperty(value="商品销售总价格")
    private BigDecimal total;

    @ApiModelProperty(value="占比")
    private BigDecimal proportion = BigDecimal.ZERO;

}
