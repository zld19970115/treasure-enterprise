package io.treasure.utils;

import io.treasure.enm.ECommission;
import io.treasure.entity.MerchantSalesRewardEntity;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {

    /**             与时间相关                */
    public static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy",Locale.CHINESE);
    public static SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINESE);
    public static SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM",Locale.CHINESE);
    public static SimpleDateFormat sdfM = new SimpleDateFormat("MM",Locale.CHINESE);
    public static SimpleDateFormat sdfd = new SimpleDateFormat("dd",Locale.CHINESE);
    public static SimpleDateFormat sdfHms = new SimpleDateFormat("HH:mm:ss",Locale.CHINESE);
    public static SimpleDateFormat sdfHm = new SimpleDateFormat("HH:mm",Locale.CHINESE);
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE);
    public static SimpleDateFormat days = new SimpleDateFormat("D",Locale.CHINESE);
    public static SimpleDateFormat week = new SimpleDateFormat("E",Locale.CHINESE);

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

    //取得月初时间
    public static Date getMonthStart(Date date) throws ParseException {
        Date res = date==null?new Date():date;
        String format = sdfYm.format(res);
        return simpleDateFormat.parse(format+"-01 00:00:00");
    }
    //取得月末时间
    public static Date getMonthEnd(Date date) throws ParseException {
        Date res = date==null?new Date():date;
        String format = sdfYm.format(res);
        String[] split = format.split("-");
        if("12".equals(split[1])){
           int  year = Integer.parseInt(split[0]);
           year++;
           format = year+"-01";
        }else{
            int month = Integer.parseInt(split[1])+1;
            if(month<10){
                format = split[0]+"-0"+month;
            }else{
                format = split[0]+"-"+month;
            }
        }
        long time = simpleDateFormat.parse(format + "-01 00:00:00").getTime()-24*60*60*1000;
        return new Date(time);
    }

    //取得上个月的相对时间
    public static Date getLastMonthDate() throws ParseException {
        Date date = new Date();
        String format = TimeUtil.simpleDateFormat.format(date);
        Integer month = Integer.parseInt(format.substring(5, 7));
        Integer days = Integer.parseInt(format.substring(8,10));
        Integer year = Integer.parseInt(format.substring(0,4));

        Date monthEnd = getMonthEnd(date);
        if(date == monthEnd){
            return date;
        }
        if(month == 1){
            month = 12;
            year --;
        }else{
            month --;
        }
        String result = year+"-"+month+"-"+"01 00:00:00";
        return TimeUtil.simpleDateFormat.parse(result);
    }


    //跨年则复位，主要是减少程序压力
    public static boolean isEnableCommissionViaDaysMethod(Date regDate,Date date, Integer dayLong){
        Map<String,Date> result = new HashMap<>();
        String sTime = "startTime";
        String eTime = "stopTime";

        Integer dLong = dayLong==null?7:dayLong;
        Date targetDate = date==null?new Date():date;

        Integer regDays = 0;
        if(regDate != null)
            regDays = Integer.parseInt(days.format(regDate));
        Integer targetDays = Integer.parseInt(days.format(targetDate));
        Integer diffDays = targetDays - regDays;

        if((diffDays/dLong)>0){
            return true;
        }

        return false;
    }
    //定时检查返佣时间
    public static boolean CommissionIsOnTime(MerchantSalesRewardEntity sysParams,Date regDate,String time) throws ParseException {
        String strTime = time == null?"00:00:00":time;
        Date preferenceDate = simpleDateFormat.parse("2020-01-01 " + strTime);

        Integer timeMode = sysParams.getTimeMode();//1数量，2开店百分比，3销量百分比
        switch (timeMode){
            case 1://每个月1号检查上个月的记录
                Date date = new Date();
                if(sdfd.format(date).equals("01")){
                    return isOnTime(preferenceDate,30);//三十分钟内
                }
                return false;
            case 2://每个星期一检查上个星期的记录
                String tmpWeek = week.format(new Date());
                if(tmpWeek.contains("一")||tmpWeek.contains("Mon")){
                    return isOnTime(preferenceDate,30);//三十分钟内
                }
                return false;
            case 3://超出定长时间时检查上一个定长时间段的内容
                return isEnableCommissionViaDaysMethod(regDate,null,null);
        }
        return false;
    }

    //取得返佣的开始时与结束时间
    public static Map<String,Date> getCommissionTimeRange(MerchantSalesRewardEntity sysParams,Date regDate) throws ParseException {
        Integer timeMode = sysParams.getTimeMode();//1数量，2开店百分比，3销量百分比
        switch (timeMode){
            case 1://1下月一号起可返上月的佣金
                return getLastMonthRange();
            case 2://2星期，下个星期可返上个星期的佣金,返回上个星期一和星期天的时间
                return getLastWeekTimeRange(null);
            case 3://3，按天数模式，比如7天,返回上一天开始的一定数量的天数
                return getLastDayRange(regDate,null,null);
        }
        return null;
    }

    public static Map<String,Date> getLastMonthRange() throws ParseException {
        Map<String,Date> result = new HashMap<>();
        String sTime = "startTime";
        String eTime = "stopTime";

        Date lastMonthDate = TimeUtil.getLastMonthDate();
        Date monthStart = TimeUtil.getMonthStart(lastMonthDate);
        Date monthEnd = TimeUtil.getMonthEnd(lastMonthDate);

        result.put(sTime,monthStart);
        result.put(eTime,monthEnd);
        return result;
    }

    public static Map<String,Date> getLastWeekTimeRange(Date date){
        Map<String,Date> result = new HashMap<>();
        String sTime = "startTime";
        String eTime = "stopTime";
        Date d = date==null?new Date():date;
        Integer weekValue = getWeekValue(d);

        long endTime = d.getTime()-24*60*60*1000*weekValue;
        long startTime = endTime - 24*60*60*1000*6;

        result.put(sTime,new Date(startTime));
        result.put(eTime,new Date(endTime));
        return result;
    }

    public static Integer getWeekValue(Date date){
        Date d = date==null?new Date():date;
        String tmpWeek = week.format(d);

        if(tmpWeek.contains("一")||tmpWeek.contains("Mon")){
            return 1;
        }
        if(tmpWeek.contains("二")||tmpWeek.contains("Tue")){
            return 2;
        }
        if(tmpWeek.contains("三")||tmpWeek.contains("Wed")){
            return 3;
        }
        if(tmpWeek.contains("四")||tmpWeek.contains("Thu")){
            return 4;
        }
        if(tmpWeek.contains("五")||tmpWeek.contains("Fri")){
            return 5;
        }
        if(tmpWeek.contains("六")||tmpWeek.contains("Sat")){
            return 6;
        }
        if(tmpWeek.contains("日")||tmpWeek.contains("Sun")){
            return 7;
        }
        return null;
    }
    //跨年则复位，主要是减少程序压力
    public static Map<String,Date> getLastDayRange(Date regDate,Date date, Integer dayLong){
        Map<String,Date> result = new HashMap<>();
        String sTime = "startTime";
        String eTime = "stopTime";

        Integer dLong = dayLong==null?7:dayLong;
        Date targetDate = date==null?new Date():date;

        Integer regDays = 0;
        if(regDate != null)
            regDays = Integer.parseInt(days.format(regDate));
        Integer targetDays = Integer.parseInt(days.format(targetDate));
        Integer diffDays = targetDays - regDays;

        if((diffDays/dLong)>0){

            int tmpValue = diffDays % dayLong;
            Long eDate = targetDate.getTime() - 24*60*60*1000*tmpValue;
            Long sDate = eDate - 24*60*60*1000*dayLong;

            result.put(sTime,new Date(sDate));
            result.put(eTime,new Date(eDate));
            return result;
        }
        return null;
    }

    public static Integer convertTimeToInt(Date target){
        String startStr = sdfHms.format(target);
        String[] split = startStr.split(":");
        if(split.length != 3)
            return 0;
        return Integer.parseInt(split[0])*100+Integer.parseInt(split[1])*10+Integer.parseInt(split[2]);
    }
    public static boolean isBetweenTime(Date start,Date stop){
        int startInt = convertTimeToInt(start);
        int stopInt = convertTimeToInt(stop);
        int nowInt = convertTimeToInt(new Date());
        if(startInt <= nowInt && stopInt >= nowInt)
            return true;
        return false;
    }
    public static Date getCurrentDateAndTime(Date date) throws ParseException {
        String hms = sdfHms.format(date);
        String ymd = sdfYmd.format(new Date());
        Date result = simpleDateFormat.parse(ymd + " " + hms);
        return result;
    }

}
