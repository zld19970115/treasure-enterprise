package io.treasure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "EChart统计图VO")
public class EChartVo {

    @ApiModelProperty(value = "数据头")
    private List<String> dataRow;

    @ApiModelProperty(value = "数据值")
    private List<String> dataCount;

}
