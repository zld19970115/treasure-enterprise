package io.treasure.service;

import io.treasure.common.utils.Result;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 收款业务
 *
 * @author super
 * @since 1.0.0 2019-09-08
 */
public interface PayService {

    Map<String, String> wxNotify(BigDecimal total_amount, String out_trade_no);
    Map<String, String> cashWxNotify(BigDecimal total_amount, String out_trade_no);
    Result aliRefund(String orderNo, String refund_fee, Long goodId);

    Map<String, String> getAliNotify(BigDecimal total_amount, String out_trade_no);

    Result wxRefund(String orderNo, String refund_fee, Long goodId);
    Result CashRefund(String orderNo, String refund_fee, Long goodId);
    Result wxRefund(String orderNo, String refund_fee);

    Result refundByGood(String payMode, String orderId, String refund_fee,Long goodId);

    Result refundByOrder(String payMode, String orderId, String refund_fee);

    Result refundByOrder(String orderId, String refund_fee);

    Result refundByGood(String orderId, String refund_fee,Long goodId);

    Map<String, String> execAliCallBack(BigDecimal total_amount, String out_trade_no);
    Map<String, String> cashExecAliCallBack(BigDecimal total_amount, String out_trade_no);
    Result CashRefund(String orderNo, String refund_fee, Long goodId);
}
