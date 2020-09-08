package io.treasure.utils;

import org.bouncycastle.util.encoders.HexEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class MerchantActivityIdGenerateUtil {



    public static String generatorMchActivityId(){

        return null;
    }

    public Map<String,Object> getInfoViaMchActivityId(String activityId){

        return  null;
    }

    /*
     x -->month,y --->date
     12345678910111213141516171819202122232425262728293031
     */
    public String parseSimpleString(){
        Date date = new Date();
        int yearSequeue = 2020;
        yearSequeue = Integer.parseInt(TimeUtil.sdfYear.format(date)) -yearSequeue;



        return null;
    }
    public String parseSimpleStringtoDate(String activityId){


        return null;
    }



}
