package io.treasure.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /**             与时间相关                */
    static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    static SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat sdfHms = new SimpleDateFormat("HH:mm:ss");
    static SimpleDateFormat sdfHm = new SimpleDateFormat("HH:mm");
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 得到凌晨5点钟的时间
     * hour指定时间
     * */
    public static boolean isOnHour(){
        String tmp = sdfHm.format(new Date());
        String[] tmps = tmp.split(":");
        int intNow = Integer.parseInt(tmps[0])*60+Integer.parseInt(tmps[1]);
        if(intNow >= 300 &&intNow<320){
            return true;
        }else{
            return false;
        }
    }
    /**      需要清台的时间范围   昨天凌晨5点      和       今天凌晨4：59         */
    public static String[] getFinishedUpdateTime() throws ParseException {
        String tmp = sdfYmd.format(new Date());
        Date specifyDate = simpleDateFormat.parse(tmp + " 04:59:00");
        long longNow = specifyDate.getTime();
        long longOnDayAgo = longNow - 60*60*24*1000+1000*60;
        String stringNow = simpleDateFormat.format(new Date(longNow));
        String stringOnDayAgo = simpleDateFormat.format(new Date(longOnDayAgo));
        //System.out.println("current:"+stringNow +",the tommow:"+stringOnDayAgo);
        String result[] = {stringOnDayAgo,stringNow};
        return result;
    }

}
