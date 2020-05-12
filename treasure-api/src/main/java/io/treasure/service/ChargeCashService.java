package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.ChargeCashDTO;
import io.treasure.entity.ChargeCashEntity;
import io.treasure.entity.ClientUserEntity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;

/**
 * 现金充值表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
public interface ChargeCashService extends CrudService<ChargeCashEntity, ChargeCashDTO> {
    Result orderSave(ChargeCashDTO dto, ClientUserEntity user) throws ParseException;
    ChargeCashDTO selectByCashOrderId(String cashOrderId);
    Map<String, String> cashNotify(BigDecimal total_amount, String out_trade_no);
}
