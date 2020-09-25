package io.treasure.utils;

import io.treasure.vo.PrizeUserInfoVo;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public static int getRandomPos(int startPos, int stopPos) {
        return ((int)(Math.random()*stopPos)+startPos);
    }

    public static List<Integer> initatorFirstPrizePosList(int startPos, int stopPos, Integer maxmum) {
        List<Integer> list = new ArrayList<>();

        while(list.size() < maxmum){
            int randomPos = getRandomPos(startPos, stopPos);
            boolean b = false;
            for(int i=0;i<list.size();i++){
                if(list.get(i)==randomPos){
                    b = true;
                }
            }
            if(!b){
                System.out.println("加入:"+randomPos);
                list.add(randomPos);
            }
        }
        return list;
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
        if(mValue >3000){
            return getRandomCoinsInRange(new BigDecimal("30"),new BigDecimal("0.01"));
        }
        int iValue = residueDegree>20?residueDegree*5:residueDegree*2;

        int res = (int)(Math.random()*(mValue-iValue))+1;
        BigDecimal resBD = new BigDecimal(res+"").divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN);
        return resBD;
    }

    public static BigDecimal getRandomCoinsInRange(BigDecimal max,BigDecimal min){
        int xValue = Integer.parseInt(max.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).toPlainString());
        int nValue = Integer.parseInt(min.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).toPlainString());

        int res = (int)(Math.random()*(xValue-nValue))+nValue;
        BigDecimal resBD = new BigDecimal(res+"").divide(new BigDecimal("100"),2,BigDecimal.ROUND_DOWN);
        return resBD;
    }
    public static String generateMobileHeader(){
        int[] ints = {132,134,135,136,137,138,139,147,150,151,152,155,157,158,159,182,183,187,188,139};
        int randomPos = getRandomPos(0, ints.length-1);
        int anInt = ints[randomPos];
        return anInt+"";
    }
    public static String generateMobileBody(){
        int[] ints = {0,1,2,3,4,5,6,7,8,9,6};
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<4;i++){
            int randomPos = getRandomPos(0, ints.length-1);
            sb.append(randomPos+"");
        }
        return sb.toString().trim();
    }
    //生成随机手机号13
    public static String randomVisualMobile(){
        StringBuilder sb = new StringBuilder();

        String header = generateMobileHeader();
        sb.append(header);
        return sb.toString()+"****"+generateMobileBody();
    }
    public static List<PrizeUserInfoVo> generateVisualMobile(int tmpNum,int maxMum,int minMum) {
        List<PrizeUserInfoVo> res = new ArrayList<>();
        for(int i=0;i<tmpNum;i++){
            String visualMobile = randomVisualMobile();
            BigDecimal randomCoinsInRange = getRandomCoinsInRange(new BigDecimal(maxMum + ""), new BigDecimal(minMum + ""));
            PrizeUserInfoVo p = new PrizeUserInfoVo();
            if(randomCoinsInRange.doubleValue()>maxMum){
                randomCoinsInRange = new BigDecimal(maxMum+"");
            }
            p.setAwardsAmount(randomCoinsInRange.doubleValue());
            p.setMobile(visualMobile);
            res.add(p);
        }
        return res;
    }

}
