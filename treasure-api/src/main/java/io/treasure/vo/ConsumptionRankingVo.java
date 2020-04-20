package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@ApiModel("查询用户订单量排行VO")
public class ConsumptionRankingVo {

    @ApiModelProperty(value="用户名称")
    private String userName;

    @ApiModelProperty(value="总单数")
    private Long count;

    @ApiModelProperty(value="完成单数总价格")
    private BigDecimal completeTotal;

    @ApiModelProperty(value="完成单数")
    private Long completeCount;

    @ApiModelProperty(value="取消单数总价格")
    private BigDecimal cancelTotal;

    @ApiModelProperty(value="取消单数")
    private Long cancelCount;

}
