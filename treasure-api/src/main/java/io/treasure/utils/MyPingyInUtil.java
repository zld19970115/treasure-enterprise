package io.treasure.utils;


import cn.hutool.core.util.PinyinUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MyPingyInUtil {



    //转成拼音
    static String toPyViaChar(char chineseChar,boolean firstWordOnly){

        int charCode = (int)chineseChar;
        if(charCode>=19968 && charCode<=40869){

            String[] strings = PinyinHelper.toHanyuPinyinStringArray(chineseChar);
            if(strings != null && strings.length >0){
                String tmp = strings[0];
                if(tmp.length()>1){
                    if(firstWordOnly){
                        tmp = tmp.charAt(0)+"";
                    }else{
                        tmp = tmp.substring(0,tmp.length()-1);
                    }

                }
                return tmp;
            }else{
                return "";
            }
        }
        return chineseChar+"";
    }

    public static String toPyString(String chineseStr,boolean firstWordOnly){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<chineseStr.length();i++){
            String tmp = toPyViaChar(chineseStr.charAt(i),firstWordOnly);
            sb.append(tmp);
        }
        return sb.toString();
    }


}
