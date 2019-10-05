package io.treasure.service;

import io.treasure.dto.ClientUserDTO;
import io.treasure.enm.Constants;
import io.treasure.entity.ClientUserEntity;

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

    String aliRefund(String orderNo, String refund_fee, Long goodId, ClientUserEntity user);

    Map<String, String> getAliNotify(BigDecimal total_amount, String out_trade_no);
}
