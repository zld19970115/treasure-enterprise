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
}
