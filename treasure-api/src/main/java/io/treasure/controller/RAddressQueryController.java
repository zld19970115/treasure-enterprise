package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.utils.Result;
import io.treasure.enm.EGeocode;
import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/re_geo")
@Api(tags="地理逆向查询")
public class RAddressQueryController {
    /**
     * 日上限3000000	并发能力1000
     *
     * 结构化地址的定义： 首先，地址肯定是一串字符，内含国家、省份、城市、区县、城镇、乡村、街道、门牌号码、屋邨、大厦等建筑物名称。
     * 按照由大区域名称到小区域名称组合在一起的字符。
     * 一个有效的地址应该是独一无二的。
     * 注意：针对大陆、港、澳地区的地理编码转换时可以将国家信息选择性的忽略，
     * 但省、市、城镇等级别的地址构成是不能忽略的。暂时不支持返回台湾省的详细地址信息。
     *
     *
     * 地理编码：将详细的结构化地址转换为高德经纬度坐标。且支持对地标性名胜景区、建筑物名称解析为高德经纬度坐标。
     * 结构化地址举例：北京市朝阳区阜通东大街6号转换后经纬度：116.480881,39.989410
     * 地标性建筑举例：天安门转换后经纬度：116.397499,39.908722
     * 逆地理编码：将经纬度转换为详细结构化的地址，且返回附近周边的POI、AOI信息。
     * 例如：116.480881,39.989410 转换地址描述后：北京市朝阳区阜通东大街6号
     *
     * 接口请求地址
     * URL: https://restapi.amap.com/v3/geocode/regeo?parameters
     * 请求方式:GET
     *
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
            //logAndLat = "125.164918,46.58033";

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
        if(mainAddress.length()<3)
            return  mainAddress;
        if(mainAddress.contains(townShip)){
            res = mainAddress.substring(index+townShip.length());
        }
        res = mainAddress;


        String substring = res.substring(0, 2);
        int i = substring.lastIndexOf(substring);
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

}
