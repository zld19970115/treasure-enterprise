package io.treasure.utils;

import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

//没使用sb管理
@NoArgsConstructor
public class SharingActivityRandomUtil {


    /*
    生成的随机数将占用总数的25~60左右，为整数
     */
    private int maxLimit;
    private int minLimit;
    private int preferenceValue;


    public SharingActivityRandomUtil(int preferenceValue){
        this.preferenceValue = preferenceValue;
        maxLimit = preferenceValue*30/100;
        minLimit = preferenceValue*1/100;
        if(minLimit<1)   minLimit = 1;
    }

    public int getRandomValue(){
        return (int)(Math.random()*(maxLimit-minLimit))+minLimit;
    }

    public int getRandomValue(int maxLimit){
        int tmp = (int)(Math.random()*(maxLimit-minLimit))+minLimit;
        if(tmp > maxLimit)
            return maxLimit;
        return tmp;
    }

    public static BigDecimal getRandomCoins(BigDecimal max,int residueDegree){
        int mValue = Integer.parseInt(max.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).toPlainString());
        int iValue = residueDegree>20?residueDegree*5:residueDegree*2;

        int res = (int)(Math.random()*(mValue-iValue))+1;
        BigDecimal resBD = new BigDecimal(res+"").divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN);
        return resBD;
    }

}
