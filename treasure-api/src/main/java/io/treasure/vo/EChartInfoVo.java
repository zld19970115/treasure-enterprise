package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "EChart统计图VO只用于统计图数据初步封装")
public class EChartInfoVo {

    @ApiModelProperty(value = "数据头")
    private String dataRow;

    @ApiModelProperty(value = "数据值")
    private String dataCount;

}
