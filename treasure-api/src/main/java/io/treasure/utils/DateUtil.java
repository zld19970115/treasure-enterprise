package io.treasure.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 获取今天
     *
     * @return String
     */
    public static String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 获取昨天
     *
     * @return String
     */
    public static String getYestoday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 获取本月开始日期
     *
     * @return String
     **/
    public static String getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 获取本月最后一天
     *
     * @return String
     **/
    public static String getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static String getWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static String getWeekEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 获取本年的第一天
     *
     * @return String
     **/
    public static String getYearStart() {
        return new SimpleDateFormat("yyyy").format(new Date()) + "-01-01";
    }

    /**
     * 获取本年的最后一天
     *
     * @return String
     **/
    public static String getYearEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date currYearLast = calendar.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(currYearLast);
    }

    /**
     * 获取当前月的上x个月，默认上一个月，返回字符串，返回格式范例：2020-04
     *
     * @return
     */
    public static String lastMonthFormatYYYYMM(Integer count) {
        if (count == null) {
            count = 1;
        }
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
        Date currdate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currdate);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - count);
        return sd.format(calendar.getTime());
    }

    public static List<String> getDaysFormatYYYYMMDD(String startTime, String endTime) {
        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (startTime == null) {
                startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                endTime = startTime;
            }
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    public static List<String> getMonths(String year) {
        if(year == null) {
            Calendar cale = Calendar.getInstance();
            year = cale.get(Calendar.YEAR)+"";
        }
        List<String> days = new ArrayList<String>();
        for(int i = 1; i <= 12; i++) {
            String s = i<10 ? "-0" : "-";
            days.add(year+s+i);
        }
        return days;
    }

    public static Date strToDate(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
        return simpleDateFormat.parse(str);
    }

}
