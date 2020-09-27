package io.treasure.controller;


import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.enm.EGeocode;
import io.treasure.geo.AddressComponent;
import io.treasure.geo.GeoregeoObj;
import io.treasure.geo.Regeocodes;
import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/re_geo")
@Api(tags="地理逆向查询")
public class RAddressQueryController {
    /**
     * 日上限3000000	并发能力1000
     * 接口请求地址(GET)
     * URL: https://restapi.amap.com/v3/geocode/regeo?parameters
     */
    private String gaodeKeyName = "jubao_2020_app";
    private String gaodeKeyValue = "248f147a3bf4681399e5593e9b30fe56";

    public String mapToString(Map<String,Object> map){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,Object> m:map.entrySet()){
            sb.append(m.getKey()+"="+m.getValue()+"&");
        }
        java.lang.String res = sb.toString();
        return res.substring(0,res.length()-1);
    }

    @GetMapping("/re_test")
    @ApiOperation("逆向地理查询")
    @ApiImplicitParam(name = "logAndLat", value = "经度逗号纬度", paramType = "query",required=true, dataType="String")
    public Result requestReGeoInfo(String logAndLat){
            int resultCode = 0;

            URL url = null;;
            HttpURLConnection conn = null;
            InputStream inputStream = null;
            InputStreamReader isReader = null;
            BufferedReader bufferedReader = null;
            Map<String,Object> map = new HashMap<>();
            map.put(EGeocode.RequestLocal.REGEO_KEY.getParamsField(),gaodeKeyValue);
        map.put(EGeocode.RequestLocal.REGEO_LOCATION.getParamsField(),logAndLat.replaceAll(" ",""));
            map.put(EGeocode.RequestLocal.regeo_RADIUS.getParamsField(),2000);
            String params = mapToString(map);
        try {
                url = new URL("https://restapi.amap.com/v3/geocode/regeo?"+params);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);//允许输出
                conn.setDoInput(true);//允许写入
                //conn.setUseCaches(false);
                //conn.setRequestProperty("Content-Type","application/json;charset=utf-8");
                conn.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                //writer.write(postData);
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {

                    inputStream = conn.getInputStream();
                    isReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(isReader);

                    StringBuilder stringBuilder = new StringBuilder();
                    String tmpLine = null;
                    while((tmpLine = bufferedReader.readLine())!= null){
                        stringBuilder.append(tmpLine);
                    }
                    System.out.println("响应地址："+stringBuilder.toString());
                    return new Result().ok(getMainString(stringBuilder.toString()));
                }
            }catch(Exception e){
                System.out.println("反地理参数处理异常 exception  ... ...");
                return new Result().error(null);
            }finally {
                try{
                    if(bufferedReader!=null) bufferedReader.close();
                }catch(Exception e){
                    System.out.println("bufferedReader_close exception  ... ...");
                    return new Result().error(null);
                }
                try{
                    if(isReader!=null) isReader.close();
                }catch(Exception e){
                    System.out.println("isReader_close exception  ... ...");
                    return new Result().error(null);
                }
                try{
                    if(inputStream!= null) inputStream.close();
                }catch(Exception e){
                    System.out.println("inputStream_close exception  ... ...");
                    return new Result().error(null);
                }
                try{
                    if(conn!=null) conn.disconnect();
                }catch(Exception e){
                    System.out.println("conn_disconnect exception  ... ...");
                    return new Result().error(null);
                }
            }
        return new Result().error(null);
    }

    public String getMainString(String s){
        String townShip = getValue(EGeocode.ResponseLocalInfo._ADDRESS_COMPONENT__TOWNSHIP,s);
        String mainAddress = getValue(EGeocode.ResponseLocalInfo.REGEO_CODES__FORMATTED_ADDRESS,s);
        int index = mainAddress.lastIndexOf(townShip);
        String res = null;
        if(mainAddress == null)
            return null;
        if(mainAddress.length()<3){
            res = mainAddress;
        }
        if(mainAddress.contains(townShip)){
            res = mainAddress.substring(index+townShip.length());
        }else{
            res = mainAddress;
        }

        String substring = res.substring(0, 2);
        int i = res.lastIndexOf(substring);
        if(i>0){
            res = res.substring(i);
        }
        return res;
    }
    public String getValue(EGeocode.ResponseLocalInfo resp,String s){
        s = s.replaceAll("\"","");
        s = s.replaceAll(" ","");
        String paramsField = resp.getParamsField();
        if(!s.contains(paramsField))
            return null;
        int i = s.lastIndexOf(paramsField);
        int j = i+1;
        StringBuilder sb = new StringBuilder();

        while(i<j){

            int pos = i+1+ paramsField.length();
            char tmpChar = s.charAt(pos);
            if(tmpChar ==','||tmpChar =='}'){
                j=0;
                System.out.println(sb.toString());
            }else{
                sb.append(tmpChar);
                i++;
                j++;
            }
        }
        return sb.toString();
    }


    /*
    restapi.amap.com/v3/geocode/regeo?key=您的key&location=116.481488,39.990464&poitype=&radius=1000&extensions=all&batch=false&roadlevel=0

     */
    @GetMapping("/re_addr")
    @ApiOperation("逆向地理查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logAndLat", value = "经度逗号纬度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "radius", value = "半径1-2-3", paramType = "query",required=false, dataType="int")
    })


    public Result requestReGeoInfoPlus(String logAndLat,Integer radius){
        int resultCode = 0;

        int rDistance = 1000;
        if(radius == 2){
            rDistance = 2000;
        }else if(radius == 3){
            rDistance = 3000;
        }

        URL url = null;;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        InputStreamReader isReader = null;
        BufferedReader bufferedReader = null;
        Map<String,Object> map = new HashMap<>();
        map.put(EGeocode.RequestLocal.REGEO_KEY.getParamsField(),gaodeKeyValue);
        map.put(EGeocode.RequestLocal.REGEO_LOCATION.getParamsField(),logAndLat.replaceAll(" ",""));
        map.put(EGeocode.RequestLocal.regeo_RADIUS.getParamsField(),rDistance);
        //map.put(EGeocode.RequestLocal.regeo_EXTENSIONS.getParamsField(),"all");

        String params = mapToString(map);
        try {
            url = new URL("https://restapi.amap.com/v3/geocode/regeo?"+params);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);//允许输出
            conn.setDoInput(true);//允许写入
            //conn.setUseCaches(false);
            //conn.setRequestProperty("Content-Type","application/json;charset=utf-8");
            conn.connect();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            //writer.write(postData);
            writer.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {

                inputStream = conn.getInputStream();
                isReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(isReader);

                StringBuilder stringBuilder = new StringBuilder();
                String tmpLine = null;
                while((tmpLine = bufferedReader.readLine())!= null){
                    stringBuilder.append(tmpLine);
                }
                System.out.println("响应地址："+stringBuilder.toString());
                return new Result().ok(fromJson(stringBuilder.toString()));
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("反地理参数处理异常 exception  ... ...");
            return new Result().error(null);
        }finally {
            try{
                if(bufferedReader!=null) bufferedReader.close();
            }catch(Exception e){
                System.out.println("bufferedReader_close exception  ... ...");
                return new Result().error(null);
            }
            try{
                if(isReader!=null) isReader.close();
            }catch(Exception e){
                System.out.println("isReader_close exception  ... ...");
                return new Result().error(null);
            }
            try{
                if(inputStream!= null) inputStream.close();
            }catch(Exception e){
                System.out.println("inputStream_close exception  ... ...");
                return new Result().error(null);
            }
            try{
                if(conn!=null) conn.disconnect();
            }catch(Exception e){
                System.out.println("conn_disconnect exception  ... ...");
                return new Result().error(null);
            }
        }
        return new Result().error(null);
    }

    public GeoregeoObj fromJson(String respString){
        Gson gson = new Gson();
        GeoregeoObj georegeoObj = gson.fromJson(respString, GeoregeoObj.class);

        return georegeoObj;
    }

}
