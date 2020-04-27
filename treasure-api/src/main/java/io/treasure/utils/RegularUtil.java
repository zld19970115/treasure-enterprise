package io.treasure.utils;

import org.apache.logging.log4j.util.Strings;

import java.util.regex.Pattern;

public class RegularUtil {

    /*校验日期连接符号是否为 - */
    public static Boolean dateSlashRegular(String date) {
        if(Strings.isBlank(date)) {
            return false;
        }
        String pattern ="\\d{4}(\\-)\\d{2}\\1\\d{2}";
        try {
            return Pattern.matches(pattern, date);
        } catch (Exception e) {
            return false;
        }
    }


}
