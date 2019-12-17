package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.exception.RenException;
import io.treasure.common.utils.Result;
import io.treasure.common.utils.WXPayUtil;
import io.treasure.config.IWXPay;
import io.treasure.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/wxNotify")
@Api(tags = "微信支付-小程序支付.")
public class ApiWXNotifyUrlController {
    @Autowired
    private IWXPay wxPay;

    @Autowired
    private PayService payService;

    @GetMapping("notify")
    @ApiOperation("微信回调测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "total_fee", value = "订单总金额(元)", required = true, paramType = "query")
    })
    public Result<String> getNotify(String total_fee, String orderNo) {
        /**
         * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
         * 推荐的做法是，当收到通知进行处理时，首先检查对应业务数据的状态，
         * 判断该通知是否已经处理过，如果没有处理过再进行处理，如果处理过直接返回结果成功。
         * 在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
         */

        // 调用业务
        String out_trade_no = orderNo;
        //单位分变成元
        BigDecimal total_amount = new BigDecimal(total_fee).divide(new BigDecimal("100"));
        Map<String, String> responseMap = payService.wxNotify(total_amount, out_trade_no);
        String responseXml = null;
        try {
            responseXml = WXPayUtil.mapToXml(responseMap);
        } catch (Exception e) {
            throw new RenException(e.getMessage());
        }
        return new Result<String>().ok(responseXml);
    }

}
