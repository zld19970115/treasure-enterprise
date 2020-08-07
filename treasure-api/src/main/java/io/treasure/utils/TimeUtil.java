package io.treasure.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeUtil {

    /**             与时间相关                */
    public static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM");
    public static SimpleDateFormat sdfM = new SimpleDateFormat("MM");
    public static SimpleDateFormat sdfd = new SimpleDateFormat("dd");
    public static SimpleDateFormat sdfHms = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat sdfHm = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 每天早上8点复位所有需要复位的内容
     * */
    public static boolean resetTaskStatusTime(){
        String tmp = sdfHm.format(new Date());
        String[] tmps = tmp.split(":");
        int intNow = Integer.parseInt(tmps[0])*60+Integer.parseInt(tmps[1]);
        if(intNow >= 480 &&intNow<500){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 得到凌晨5点钟的时间
     * hour指定时间
     * */
    public static boolean isClearTime(){
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
        String result[] = {stringOnDayAgo,stringNow};
        return result;
    }

    public static String dateToStamp(Date date) throws ParseException{
        //Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return String.valueOf(ts);
    }

    //针对每天定时判断
    public static boolean isOnTime(Date target,int delayMinute){
        String targetString = sdfHm.format(target);
        String[] targetArray = targetString.split(":");
        int startTime = Integer.parseInt(targetArray[0])*60+Integer.parseInt(targetArray[1]);
        int stopTime = startTime+delayMinute;

        String tmp = sdfHm.format(new Date());
        String[] tmps = tmp.split(":");
        int intNow = Integer.parseInt(tmps[0])*60+Integer.parseInt(tmps[1]);
        if(intNow >= startTime &&intNow<stopTime){
            return true;
        }else{
            return false;
        }
    }

    //针对每月定时判断(每月1号)
    public static boolean isOnMothDay(Date target){
        String targetString = sdfd.format(target);
        String tmp = sdfd.format(new Date());
        if(targetString.equals(tmp))
            return true;
        return false;
    }

    //static List<Date>年月取当前的年月
    public  static List<Date>  generatorTimeRangeParams(Date target,int delayMonth) throws ParseException {

        if(delayMonth>11)
            delayMonth = 11;

        List<Date> res = new ArrayList<>();

        String tmp = simpleDateFormat.format(target);
        String yearAndMonth = sdfYm.format(new Date());

        String[] split = yearAndMonth.split("-");
        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        boolean moreThanOneYear = false;
        if((month -delayMonth)>0){
            month -=delayMonth;
        }else{
            month = month+12-delayMonth;
            year--;
        }

        String resString = year+"-"+month + tmp.substring(7);
        if(month<10){
            resString = year+"-0"+month + tmp.substring(7);
        }
        Date start = simpleDateFormat.parse(resString);
        Date stop = simpleDateFormat.parse(yearAndMonth+simpleDateFormat.format(target).substring(7));

        res.add(start);
        res.add(stop);
        return res;
    }


}
