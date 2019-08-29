package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Data
@ApiModel(value = "完整订单数据")
public class AllOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主订单数据")
    private MasterOrderDTO masterOder;

    @ApiModelProperty(value = "订单菜品数据")
    private List<SlaveOrderDTO> slaveOrders;
}
