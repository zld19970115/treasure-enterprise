package io.treasure.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderUtil {

    /**
     * 订单编号
     * @param id
     * @return
     */
    public static String getOrderIdByTime(Long id) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());

        String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(10);
        }
        return "D"+id.toString()+newDate+result;
    }

    /**
     * 退款单编号
     * @param id
     * @return
     */
    public static String getRefundOrderIdByTime(Long id) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());
        String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(10);
        }
        return "T"+id.toString()+newDate+result;
    }
}
