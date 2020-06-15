package io.treasure.utils;

import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.stereotype.Component;

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
        maxLimit = preferenceValue*40/100;
        minLimit = preferenceValue*15/100;

    }

    public int getRandomValue(){
        return (int)(Math.random()*(maxLimit-minLimit))+minLimit;
    }


}
