package io.treasure.controller;


import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.config.IWXConfig;
import io.treasure.config.IWXPay;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.service.RefundOrderService;
import io.treasure.service.SlaveOrderService;
import io.treasure.utils.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付 - 通用API.
 *
 * <p>
 * 类似支付宝中的条码支付.
 *
 * @author super
 * @version 1.0
 * @since 2019/3/28
 */
@Slf4j
@RestController
@RequestMapping("/api/wx")
@Api(tags="微信通用接口")
public class ApiWXPayController {

    @Autowired
    private IWXPay wxPay;

    @Autowired
    private IWXConfig wxPayConfig;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private SlaveOrderService slaveOrderService;



    /**
     * 订单查询
     * @param orderNo
     * @return
     * @throws Exception
     */
    @GetMapping("/orderQuery")
    @ApiOperation(value="订单查询")
    public Object orderQuery(String orderNo) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", orderNo);
        Map<String, String> result = wxPay.orderQuery(data);

        return result;
    }

    /**
     * 退款
     * 注意：调用申请退款、撤销订单接口需要商户证书
     * 注意：沙箱环境响应结果可能会是"沙箱支付金额不正确,请确认验收case"，但是正式环境不会报这个错误
     * 微信支付的最小金额是0.1元，所以在测试支付时金额必须大于0.1元，否则会提示微信支付配置错误，可以将microPay的total_fee大于1再退款
     */
    @Login
    @PostMapping("/refund")
    @ApiOperation(value="退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",required=true,paramType="query"),
            @ApiImplicitParam(name="total_fee",value="订单总金额单位分",required=true,paramType="query"),
            @ApiImplicitParam(name="refund_fee",value="退款金额单位分",required=true,paramType="query")
    })
    public Object refund(String orderNo,String total_fee,String refund_fee,Long goodId,@LoginUser ClientUserEntity user) throws Exception {
        Map<String, String> reqData = new HashMap<>();
        // 商户订单号
        reqData.put("out_trade_no", orderNo);
        // 授权码
        reqData.put("out_refund_no", OrderUtil.getRefundOrderIdByTime(user.getId()));

        // 订单总金额，单位为分，只能为整数
        BigDecimal totalAmount = new BigDecimal(total_fee);
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        java.text.DecimalFormat df=new java.text.DecimalFormat("0");
        reqData.put("total_fee", df.format(total));
        //退款金额
        BigDecimal refundAmount = new BigDecimal(refund_fee);
        BigDecimal refund = refundAmount.multiply(new BigDecimal(100));  //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        reqData.put("refund_fee", df.format(refund));
        // 退款异步通知地址
        reqData.put("notify_url", wxPayConfig.getNotifyUrl());
        reqData.put("refund_fee_type", "CNY");
        reqData.put("op_user_id", wxPayConfig.getMchID());

        Map<String, String> resultMap = wxPay.refund(reqData);


        //将退款ID更新到refundOrder表中refund_id
        refundOrderService.updateRefundId(OrderUtil.getRefundOrderIdByTime(user.getId()),orderNo,goodId);

        //将退款ID更新到订单菜品表中
        slaveOrderService.updateRefundId(OrderUtil.getRefundOrderIdByTime(user.getId()),orderNo,goodId);


        log.info(resultMap.toString());

        return resultMap;
    }


    /**
     * 退款查询
     * @param orderNo
     * @return
     * @throws Exception
     */
    @GetMapping("/refundQuery")
    @ApiOperation(value="退款查询")
    public Object refundQuery(String orderNo) throws Exception {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", orderNo);
        Map<String, String> result = wxPay.refundQuery(reqData);

        return result;
    }


    /**
     * 下载对账单
     * 注意：
     *      微信在次日9点启动生成前一天的对账单，建议商户10点后再获取；
     *      对账单接口只能下载三个月以内的账单。
     * 下载对账单：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_6
     * @throws Exception
     */
    @Login
    @PostMapping("/downloadBill")
    @ApiOperation(value="下载对账单")
    public Object downloadBill(String billDate) throws Exception {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("bill_date", billDate);
        reqData.put("bill_type", "ALL");
        Map<String, String> resultMap = wxPay.downloadBill(reqData);

        return resultMap;
    }

}
