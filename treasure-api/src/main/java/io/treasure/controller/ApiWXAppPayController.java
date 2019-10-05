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
import io.treasure.dto.OrderDTO;
import io.treasure.enm.Constants;
import io.treasure.service.MasterOrderService;
import io.treasure.service.PayService;
import io.treasure.utils.AdressIPUtil;
import io.treasure.utils.PayCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private PayService payService;

    @Autowired
    private MasterOrderService masterOrderService;

    /**
     * @param total_fee    支付金额
     * @param description    描述
     * @param request
     * @return
     */
//    @Login
    @PostMapping("/wxpay")
    @ApiOperation(value="微信支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",required=true,paramType="query"),
            @ApiImplicitParam(name="total_fee",value="订单总金额(元)",required=true,paramType="query"),
            @ApiImplicitParam(name="description",value="产品描述",required=true,paramType="query"),
    })
    public Result wxpay(HttpServletRequest request,String total_fee, String orderNo, String description) throws Exception {
        Result result = new Result();
        OrderDTO orderDTO=masterOrderService.getOrder(orderNo);
        if(orderDTO.getStatus().intValue()!= Constants.OrderStatus.NOPAYORDER.getValue()){
            return result.error(-1,"非未支付订单，请选择未支付订单支付！");
        }
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
            params.put("appid", wxPayConfig.getAppID());
            params.put("partnerid", wxPayConfig.getMchID());
            Long time = (System.currentTimeMillis() / 1000);
            params.put("timestamp", time.toString());
            params.put("noncestr", orderInfo.get("nonce_str"));
            params.put("prepayid", orderInfo.get("prepay_id"));
            params.put("package", "Sign=WXPay");
            String sign = WXPayUtil.generateSignature(params, wxPayConfig.getKey());

            Map<String, String> reslutMap = new HashMap<>();
            reslutMap.put("appid",wxPayConfig.getAppID());
            reslutMap.put("partnerid",wxPayConfig.getMchID());
            reslutMap.put("timestamp",time.toString());
            reslutMap.put("noncestr",orderInfo.get("nonce_str"));
            reslutMap.put("prepayid",orderInfo.get("prepay_id"));
            reslutMap.put("package","Sign=WXPay");
            reslutMap.put("sign", sign);
            result.ok(reslutMap);
        }
        return result;
    }

    /**
     * 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/notify")
    @ApiOperation(value="回调地址")
    public void precreateNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("----------------pda微信回调-------" + request);
        Map<String, String> reqData = wxPay.getNotifyParameter(request);
        System.out.println("----------------pda微信回调Map-------" + reqData);
        /**
         * {
         * transaction_id=4200000138201806180751222945,
         * nonce_str=aaaf3fe4d3aa44d8b245bc6c97bda7a8,
         * bank_type=CFT,
         * openid=xxx,
         * sign=821A5F42F5E180ED9EF3743499FBCF13,
         * fee_type=CNY,
         * mch_id=xxx,
         * cash_fee=1,
         * out_trade_no=186873223426017,
         * appid=xxx,
         * total_fee=1,
         * trade_type=NATIVE,
         * result_code=SUCCESS,
         * time_end=20180618131247,
         * is_subscribe=N,
         * return_code=SUCCESS
         * }
         */
        log.info(reqData.toString());

        // 特别提醒：商户系统对于支付结果通知的内容一定要做签名验证,并校验返回的订单金额是否与商户侧的订单金额一致，防止数据泄漏导致出现“假通知”，造成资金损失。
        boolean signatureValid = wxPay.isPayResultNotifySignatureValid(reqData);
        if (signatureValid) {
            /**
             * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
             * 推荐的做法是，当收到通知进行处理时，首先检查对应业务数据的状态，
             * 判断该通知是否已经处理过，如果没有处理过再进行处理，如果处理过直接返回结果成功。
             * 在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
             */

            // 调用业务
            String out_trade_no = reqData.get("out_trade_no");
            //单位分变成元
            BigDecimal total_amount = new BigDecimal(reqData.get("total_fee")).divide(new BigDecimal("100"));
            Map<String, String> responseMap = payService.wxNotify(total_amount,out_trade_no);
            String responseXml = WXPayUtil.mapToXml(responseMap);
            response.setContentType("text/xml");
            response.getWriter().write(responseXml);
            response.flushBuffer();
        }
    }

}
