package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "EChart统计图订单增长VO")
public class EChartOrderVo {

    @ApiModelProperty(value = "数据头")
    private List<String> dataRow;

    @ApiModelProperty(value = "主订单")
    private List<String> porderCount;

    @ApiModelProperty(value = "子订单")
    private List<String> orderCount;

}
