package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "EChart统计图订单增长初步封装")
public class EChartOrderInfoVo {

    @ApiModelProperty(value = "数据头")
    private String dataRow;

    @ApiModelProperty(value = "主订单")
    private String porderCount;

    @ApiModelProperty(value = "子订单")
    private String orderCount;

}
