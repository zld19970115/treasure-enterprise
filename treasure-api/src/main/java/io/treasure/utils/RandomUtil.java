package io.treasure.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static jdk.nashorn.internal.objects.NativeMath.random;

/**
 * @author user
 * @title: 随机数工具包
 * @projectName treasure-enterprise
 * @description: TODO
 * @date 2020/6/88:07
 */
public class RandomUtil {

//    public String test(int target){
//
//        boolean theLast = false;
//        if(theLast)
//            return target+"";
//        return randomDemo(200);
//    }
//
//    public String randomDemo(int target){
//        String s = Math.random()*(target*50)+"";
//        return s.substring(0,s.indexOf("."));
//
//
//    }
    public static String randowPower(double total, int num, double min) {

        StringBuffer buf=new StringBuffer();
        for (int i = 1; i < num; i++) {
            double safe_total = (total - (num - i) * min) / (num - i);
            double money = Math.random() * (safe_total - min) + min;
            BigDecimal money_bd = new BigDecimal(money);
            money = money_bd.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            total = total - money;
            BigDecimal total_bd = new BigDecimal(total);
            total = total_bd.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            buf.append(money+",");
            System.out.println("第" + i + "个：" + money + ",余额为:" + total + "元");
        }
        System.out.println("第"+num+"个红包："+total+",余额为:0元");
        buf.append(total+"");
        return buf.toString();
    }

    static void hb(double total,int num,double min){
        for(int i=1;i<num;i++){
            double safe_total=(total-(num-i)*min)/(num-i);
            double money=Math.random()*(safe_total-min)+min;
            BigDecimal money_bd=new BigDecimal(money);
            money=money_bd.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
            total=total-money;
            BigDecimal total_bd=new BigDecimal(total);
            total=total_bd.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
            System.out.println("第"+i+"个红包："+money+",余额为:"+total+"元");
        }
        System.out.println("第"+num+"个红包："+total+",余额为:0元");
    }



    public static String random8(){
        int  maxNum = 7;
        int i;
        int count = 0;
        char[] str = {'1', '2', '3', '4', '5', '6', '7', '8', '9' };
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        pwd.append("1");
        while(count < 12){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 1 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return pwd.toString();
    }

}
