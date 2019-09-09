package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.constant.WXPayConstants;
import io.treasure.common.utils.WXPayUtil;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付-扫码支付.
 * <p>
 * detailed description
 *
 * @author super
 * @version 1.0
 * @since 2019/3/28
 */
@Slf4j
@RestController
@RequestMapping("/api/wxprecreate")
@Api(tags="微信支付-扫码支付")
public class ApiWXPayPrecreateController {
    @Autowired
    private IWXPay wxPay;

    @Autowired
    private IWXConfig wxPayConfig;

    @Autowired
    private PayService payService;


    /**
     * 扫码支付 - 统一下单
     * 相当于支付不的电脑网站支付
     *
     * <a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1">扫码支付API</a>
     */
    @Login
    @PostMapping("/order")
    @ApiOperation(value="扫码支付")
    public String precreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> reqData = new HashMap<>();
        //(必填)商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        reqData.put("out_trade_no", String.valueOf(System.nanoTime()));
        //(必填)交易类型
        reqData.put("trade_type", "NATIVE");
        //(必填)trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。
        reqData.put("product_id", "1");
        //(必填)商品简单描述，该字段请按照规范传递，具体请见参数规定
        reqData.put("body", "商户下单");
        // (必填)订单总金额，单位为分
        reqData.put("total_fee", "2");
        // (必填)APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
        reqData.put("spbill_create_ip", "14.23.150.211");
        // (必填)异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        reqData.put("notify_url", wxPayConfig.getNotifyUrl());
        // （可填）自定义参数, 可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
        reqData.put("device_info", "");
        // （可填）附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
        reqData.put("attach", "");

        /**
         * {
         * code_url=weixin://wxpay/bizpayurl?pr=vvz4xwC,
         * trade_type=NATIVE,
         * return_msg=OK,
         * result_code=SUCCESS,
         * return_code=SUCCESS,
         * prepay_id=wx18111952823301d9fa53ab8e1414642725
         * }
         */
        Map<String, String> responseMap = wxPay.unifiedOrder(reqData);
        log.info(responseMap.toString());
        String returnCode = responseMap.get("return_code");
        String resultCode = responseMap.get("result_code");
        String codeUrl="";
        if (WXPayConstants.SUCCESS.equals(returnCode) && WXPayConstants.SUCCESS.equals(resultCode)) {
            String prepayId = responseMap.get("prepay_id");
            codeUrl = responseMap.get("code_url");

//            BufferedImage image = PayUtil.getQRCodeImge(codeUrl);
//
//            response.setContentType("image/jpeg");
//            response.setHeader("Pragma","no-cache");
//            response.setHeader("Cache-Control","no-cache");
//            response.setIntHeader("Expires",-1);
//            ImageIO.write(image, "JPEG", response.getOutputStream());
        }
        return codeUrl;
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
        Map<String, String> reqData = wxPay.getNotifyParameter(request);

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
