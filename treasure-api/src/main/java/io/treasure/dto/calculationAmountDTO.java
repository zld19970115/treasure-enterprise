package io.treasure.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * 计算赠送金和优惠卷菜品信息返回参数
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "计算赠送金和优惠卷菜品信息返回参数")
public class calculationAmountDTO extends ComparableCondition implements Comparable<calculationAmountDTO>{

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
     * 优惠后价格(单价)
     */
    private BigDecimal newPrice;


    /**
     * 平台扣点金额
     */
    private BigDecimal platformBrokerage;

    /**
     * 商户实际所得金额（扣除平台扣点不包含赠送金）
     */
    private BigDecimal merchantProceeds;

//    public calculationAmountDTO updateFranctionPart(BigDecimal franctionPart){
//        super.setFractionPart(fractionPart);
//        return this;
//    }
    public int compareField(calculationAmountDTO t){
        int res = 0;
        if(this.getFractionPart().compareTo(t.getFractionPart())>0){
            return -1;
        }else if(this.getFractionPart().compareTo(t.getFractionPart())<0){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public int compareTo(calculationAmountDTO t) {
        return compareField(t);
    }

}
