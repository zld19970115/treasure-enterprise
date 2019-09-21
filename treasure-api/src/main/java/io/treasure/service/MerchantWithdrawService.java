package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.entity.MerchantWithdrawEntity;

import java.math.BigDecimal;

/**
 * 提现表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-20
 */
public interface MerchantWithdrawService extends CrudService<MerchantWithdrawEntity, MerchantWithdrawDTO> {
    //修改状态
    void updateStatusById(long id,int status);

    BigDecimal selectTotalCath(long  martId);
    Double selectByMartId(long  martId);

    Double selectAlreadyCash(long  martId);
}