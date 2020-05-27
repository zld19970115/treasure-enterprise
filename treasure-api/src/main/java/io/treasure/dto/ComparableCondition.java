package io.treasure.dto;

import lombok.Getter;

import java.math.BigDecimal;

public class ComparableCondition {

    @Getter
    String fieldName = "getMerchantProceeds";
    BigDecimal totalItemx = new BigDecimal("0");
    BigDecimal fractionPart = new BigDecimal("0");
    //单项在总体中的占比
    BigDecimal overAllRatio;

    public enum SortRule{
        getFreeGold,//赠送金
        getDiscountsMoney,//优惠券
        getPlatformBrokerage,
        getMerchantProceeds,
        getFractionPart,
        nothing;
    }

    public BigDecimal getOverAllRatio() {
        return overAllRatio;
    }

    public void setOverAllRatio(BigDecimal overAllRatio) {
        this.overAllRatio = overAllRatio;
    }

    public BigDecimal getTotalItemx() {
        return totalItemx;
    }

    public void setTotalItemx(BigDecimal totalItemx) {
        this.totalItemx = totalItemx;
    }

    public void setFieldName(SortRule sortRule){
        fieldName = sortRule.name();
    }

    public BigDecimal getFractionPart() {
        return fractionPart;
    }

    public void setFractionPart(BigDecimal fractionPart) {
        this.fractionPart = fractionPart;
    }
}
