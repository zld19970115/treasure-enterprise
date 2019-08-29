package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.utils.Result;
import io.treasure.common.utils.WXPayUtil;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.treasure.utils.AdressIPUtil;
import io.treasure.utils.PayCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
@RestController
@RequestMapping("/api/appPay")
@Api(tags="微信支付-APP支付.")
public class ApiWXAppPayController {

    @Autowired
    private IWXPay wxPay;

    @Autowired
    private IWXConfig wxPayConfig;

    /**
     * @param total_fee    支付金额
     * @param description    描述
     * @param request
     * @return
     */
    @Login
    @PostMapping("/wxpay")
    @ApiOperation(value="微信支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",required=true,paramType="query"),
            @ApiImplicitParam(name="total_fee",value="订单总金额(元)",required=true,paramType="query"),
            @ApiImplicitParam(name="description",value="产品描述",required=true,paramType="query"),
    })
    public Result wxpay(HttpServletRequest request,String total_fee, String orderNo, String description) throws Exception {
        Result result = new Result();
        HashMap<String, String> data = new HashMap<>();
        data.put("body", description);
        data.put("out_trade_no", orderNo);
//        data.put("device_info", "");//调用接口提交的终端设备号
        data.put("fee_type", "CNY");
        //因为是外币，这里做汇率转换
        BigDecimal totalAmount = new BigDecimal(total_fee);
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        data.put("total_fee",df.format(total));
        data.put("spbill_create_ip", AdressIPUtil.getClientIpAddress(request));
        data.put("notify_url", wxPayConfig.getNotifyUrl());
        data.put("trade_type", "APP");//支付类型
        data.put("attach","ZF");//订单附加信息 (业务需要数据，自定义的)
        Map<String, String> orderInfo= wxPay.unifiedOrder(data);
        if(orderInfo!=null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("appId", wxPayConfig.getAppID());
            params.put("mchId", wxPayConfig.getMchID());
            Long time = (System.currentTimeMillis() / 1000);
            params.put("timeStamp", time.toString());
            params.put("nonceStr", orderInfo.get("nonce_str"));
            params.put("prepayId", orderInfo.get("prepay_id"));
            params.put("package", "Sign=WXPay");
            params.put("signType", "MD5");
            String sign = WXPayUtil.generateSignature(params, wxPayConfig.getKey());

            Map<String, String> reslutMap = new HashMap<>();
            reslutMap.put("appId",orderInfo.get("appid"));
            reslutMap.put("code_url",orderInfo.get("code_url"));
            reslutMap.put("mch_id",orderInfo.get("mch_id"));
            reslutMap.put("nonce_str",orderInfo.get("nonce_str"));
            reslutMap.put("prepay_id",orderInfo.get("prepay_id"));
            reslutMap.put("result_code",orderInfo.get("result_code"));
            reslutMap.put("return_msg",orderInfo.get("return_msg"));
            reslutMap.put("return_code",orderInfo.get("return_code"));
            reslutMap.put("pack","Sign=WXPay");
            reslutMap.put("time",time.toString());
            reslutMap.put("sign", sign);//官方文档上是sign，当前示例代码是paySign 可能以前的
            reslutMap.put("err_code_des", orderInfo.get("err_code_des"));
            result.ok(reslutMap);
        }
        return result;
    }

}
