package io.treasure.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderUtilTwo {

    /*
        生成新的订单号--区分107和248服务器，107订单以7结尾，248以8结尾
     */
    public static enum AddrServer{
        ONE_ZERO_SEVEN,
        TWO_FOUR_EIGHT;
    }
    public static String getOrderIdByTime(Long id,AddrServer addrServer) {
        return "D"+id.toString()+baseNumber(addrServer);
    }

    public static String getRefundOrderIdByTime(Long id,AddrServer addrServer) {
        return "T"+id.toString()+baseNumber(addrServer);
    }
    public static String baseNumber(AddrServer addrServer){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String newDate=sdf.format(new Date());

        String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(9);
        }
        if(addrServer.name().equals(AddrServer.ONE_ZERO_SEVEN.name()))
            return newDate+result+"7";
        return newDate+result+"8";
    }

}
