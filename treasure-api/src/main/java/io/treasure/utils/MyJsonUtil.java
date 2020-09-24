package io.treasure.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MyJsonUtil {

    @Test
    public void test(){
        List<String> t = getValue("this is a testhellowold","test");
    }
    public List<String> getValue(String jsonString, String jsonKey){
        List<String> result = new ArrayList<>();
        if(!jsonString.contains(jsonKey))
            return null;

        int i = jsonString.lastIndexOf(jsonKey)+jsonKey.length();
        StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        int braces = 0;//大扩号
        int squareBrackets = 0;//方扩号
        int stopIndex = 0;
        for(;i<jsonString.length();i++){
            char c = jsonString.charAt(i);
            if(startIndex != 0){
                if(c == '{'){
                    braces++;
                }else if(c == '}'){
                    braces--;
                }

            }else{
                if(c == ':')
                    startIndex = i+1;//初值
            }

            System.out.println(jsonString.charAt(i));
        }

        for(int ix=0;ix<jsonString.length();ix++){
            if(jsonString.charAt(ix) == ' '){
                System.out.println( "空格");
            }else{
                System.out.println(jsonString.charAt(ix));
            }
        }        //System.out.println(jsonString.charAt(i));

        return result;
    }
    public String setValue(String jsonString,String value,String type){
        return null;
    }
}
