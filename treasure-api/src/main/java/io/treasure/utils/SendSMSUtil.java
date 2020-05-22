package io.treasure.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.sms.SMSSend;
import io.treasure.common.utils.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信工具类
 */
public class SendSMSUtil {

    /**
     * 新订单通知
     * @param phoneNumber
     * @param smsConfig
     * @return
     */
    public static boolean sendNewOrder(String phoneNumber, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179295541", null);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 商家接单通知
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean sendMerchantReceipt(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179285535", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 商家拒单通知
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean sendMerchantRefusal(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179280542", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 申请退菜通知
     * @param phoneNumber
     * @param smsConfig
     * @return
     */
    public static boolean sendApplyRefusal(String phoneNumber, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179280544", null);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 商家同意退菜通知
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean sendMerchantAgreeRefusal(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179555005", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 商家拒绝退菜通知
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean sendMerchantRefusalFood(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179545007", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 用户申请退款通知
     * @param phoneNumber
     * @param smsConfig
     * @return
     */
    public static boolean sendApplyRefund(String phoneNumber, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179560006", null);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 商家同意退款通知
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean sendAgreeRefund(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179555007", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 商家拒绝退款通知
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean sendRefuseRefund(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_179545011", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }

    /**
     * 商家入驻通知平台管理员
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean MerchantsSettlement(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_190791855", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }
    /**
     * 商家申请提现通知平台
     * @param phoneNumber     手机号
     * @param merchantName    商户名称
     * @param smsConfig       配置
     * @return
     */
    public static boolean MerchantsWithdrawal(String phoneNumber, String merchantName, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        Map map=new HashMap();
        map.put("name",merchantName);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_190791860", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
        }
        return ret;
    }


    /**
     * 获取验证码
     * @param phoneNumber
     * @param request
     * @param smsConfig
     * @return
     */
    public static boolean sendCodeForRegister(String phoneNumber, HttpServletRequest request, SMSConfig smsConfig) {
        boolean ret=false;
        SMSSend send=new SMSSend(smsConfig);
        String number = RandomUtil.randomNumbers(6);
        Map map=new HashMap();
        map.put("code",number);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_165340734", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            ret=true;
            request.getSession().setAttribute("code"+phoneNumber,number);
            request.getSession().setAttribute("time"+phoneNumber,System.currentTimeMillis());
            System.out.println(request.getSession().getAttribute("time"+phoneNumber)+"akjasdhkashkdhakdhsalfldsflj还得是绿肥红瘦大号返回多索拉卡啥快递计划付款说得好好看到沙发开还得是可视对讲回访客户山东航空还是得");
        }
        return ret;
    }
    /**
     * 获取解绑验证码
     * @param phoneNumber
     * @param smsConfig
     * @return
     */
    public static Result sendCodeFordeletWx(String phoneNumber, SMSConfig smsConfig) {
        Result result=new Result();
        SMSSend send=new SMSSend(smsConfig);
        String number = RandomUtil.randomNumbers(6);
        Map map=new HashMap();
        map.put("code",number);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_189835466", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            result.ok(number);
        }
        return result;
    }
    /**
     * 获取注销验证码
     * @param phoneNumber
     * @param smsConfig
     * @return
     */
    public static Result sendCodeFordeletzhuxiao(String phoneNumber, SMSConfig smsConfig) {
        Result result=new Result();
        SMSSend send=new SMSSend(smsConfig);
        String number = RandomUtil.randomNumbers(6);
        Map map=new HashMap();
        map.put("code",number);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_165340693", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            result.ok(number);
        }
        return result;
    }
    /**
     * 获取验证码
     * @param phoneNumber
     * @param smsConfig
     * @return
     */
    public static Result sendCodeForRegister(String phoneNumber, SMSConfig smsConfig) {
        Result result=new Result();
        SMSSend send=new SMSSend(smsConfig);
        String number = RandomUtil.randomNumbers(6);
        Map map=new HashMap();
        map.put("code",number);
        String template= JSON.toJSONString(map);
        String data=send.send(phoneNumber, "聚宝科技", "SMS_165340734", template);
        JSONObject jsonObject=JSONObject.parseObject(data);
        String code=jsonObject.get("Code").toString();
        if("OK".equals(code)){
            result.ok(number);
        }
        return result;
    }

    /**
     *
     * @param phoneNumber
     * @param request
     * @param code
     * @return
     */
    public static Result verifyCode(String phoneNumber, HttpServletRequest request, String  code){
        Result result=new Result();
        String codeing= (String) request.getSession().getAttribute("code"+phoneNumber);
        Long time= (Long) request.getSession().getAttribute("time"+phoneNumber);
        request.getSession().removeAttribute("code"+phoneNumber);
        request.getSession().removeAttribute("time"+phoneNumber);
        if(codeing==null||"".equals(codeing.trim())){
            result.error(-1,"验证码为空！");
        }
        System.out.println(codeing+"sahdhakshdkhaskdhkhsjhaf了萨达了金坷垃绝对路径as拉斯绝对路径爱上了昆仑决撒了大家萨克埃里克森几点啦可视对讲爱是快乐大姐说了肯定奥斯卡单机啊胜利大街");
        System.out.println(time+"sahdhakshdkhaskdhkhsjhaf积分接口了萨达了金坷垃绝对路径as拉斯绝对路径爱上了昆仑决撒了大家萨克埃里克森几点啦可视对讲爱是快乐大姐说了肯定奥斯卡单机啊胜利大街");
        if((System.currentTimeMillis()-time)/1000/60>=10){
            result.error(-2,"验证码已经过期！");
        }
        if(code.equalsIgnoreCase(codeing.trim())){
            result.ok("验证码一致");
        }else{
            result.error(-3,"验证码不一致");
        }
        return result;
    }
}
