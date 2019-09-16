package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计算赠送金和优惠卷菜品信息返回参数
 */
@Data
@ApiModel(value = "计算赠送金和优惠卷菜品信息返回参数")
public class calculationAmountDTO {

    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 菜品名称
     */
    private String name;
    /**
     * 菜品图片
     */
    private String icon;

    /**
     * 赠送金金额（此菜品已经使用的赠送金抵扣的金额）
     */
    private BigDecimal freeGold;


    /**
     * 优惠卷金额（此菜品已经使用的优惠卷抵扣的金额）
     */
    private BigDecimal discountsMoney;

    /**
     * 总金额
     */
    private BigDecimal totalMoney;

    /**
     * 菜品ID
     */
    private Long goodId;

    /**
     * 优惠后价格
     */
    private BigDecimal newPrice;


}
