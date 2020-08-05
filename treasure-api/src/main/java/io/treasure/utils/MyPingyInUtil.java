package io.treasure.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import org.junit.Test;

public class MyPingyInUtil {

    @Test
    public void test(){
        for(int i=15878;i<19968;i++){
            System.out.print((char)i+"="+toPyViaChar((char)i,false));
        }

    }

    //转成拼音
    static String toPyViaChar(char chineseChar,boolean firstWordOnly){

        int charCode = (int)chineseChar;
        if(charCode == 15878){
            if(firstWordOnly)
                return "k";
            return "kao";
        }
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
