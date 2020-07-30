package io.treasure.utils;

import cn.hutool.core.comparator.PinyinComparator;
import cn.hutool.core.util.PinyinUtil;
import org.junit.Test;
import sun.nio.cs.UnicodeEncoder;

public class Utf8ToPinyinUtil {

    @Test
    public void test(){
        toFirstChar("钟山");
        //加拼音32字符
    }

    public static String toFirstChar(String chineseString){
        String pinyinStr = null;
        char[] signChar = chineseString.toCharArray();
        Character character = Character.valueOf(signChar[0]);
        PinyinComparator pinyinComparator = new PinyinComparator();
        String haha = PinyinUtil.getAllFirstLetter("中国人");
        String xx = PinyinUtil.getPinYin("铁板酸菜五花肉");

        System.out.println("chracter"+character+","+xx);
        return null;
    }
}
