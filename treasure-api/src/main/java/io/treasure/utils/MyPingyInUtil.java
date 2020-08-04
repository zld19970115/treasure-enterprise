package io.treasure.utils;


import cn.hutool.core.util.PinyinUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MyPingyInUtil {

    //转成拼音
    public static String toFullPinyinString(String chineseStr){
        return PinyinUtil.getPinYin(chineseStr);
    }
    //转成拼音首字母
    public static String toAbbreviatePinyinString(String chineseStr){
        return PinyinUtil.getAllFirstLetter(chineseStr);
    }

    public static enum pinyinKey{
        fullPy,abbreviatePy,
    }

    public static Map<String,String> toPinyinComboString(String chineseStr){

        Map<String,String> res = new HashMap<>();
        res.put(pinyinKey.fullPy.name(),PinyinUtil.getPinYin(chineseStr));
        res.put(pinyinKey.abbreviatePy.name(),PinyinUtil.getAllFirstLetter(chineseStr));

        return res;
    }
}
