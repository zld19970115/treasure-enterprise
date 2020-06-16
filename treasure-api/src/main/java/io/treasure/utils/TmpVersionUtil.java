package io.treasure.utils;

public class TmpVersionUtil {

    private static Long allowIntervalTime = 500L;

    public static void setAllowIntervalTime(Long allowIntervalTime){
        TmpVersionUtil.allowIntervalTime = allowIntervalTime;
    }
    public static Long getAllowIntervalTime(){
        return allowIntervalTime;
    }

}
