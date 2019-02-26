package io.treasure.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.sms.SMSSend;
import io.treasure.common.utils.Result;
import io.treasure.config.ISMSConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信工具类
 */
public class SendSMSUtil {
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
        }
        return ret;
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
