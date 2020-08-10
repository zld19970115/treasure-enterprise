package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@ApiModel(value = "等级领取宝币记录表")
public class levelStatusDTO  implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "userId")
    private Long userId;
    @ApiModelProperty(value = "leveled")
    private Integer leveled;
    @ApiModelProperty(value = "blance")
    private BigDecimal blance;
}
