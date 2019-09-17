package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;

import io.treasure.common.service.CrudService;
import io.treasure.entity.SlaveOrderEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface MasterOrderService extends CrudService<MasterOrderEntity, MasterOrderDTO> {
    void updateStatusAndReason(long id, int status, long verify, Date verify_date, String verify_reason);

    OrderDTO getOrder(String orderId);

    Result orderSave(OrderDTO dto, List<SlaveOrderEntity> dtoList, ClientUserEntity user);

    PageData<OrderDTO> listPage(Map<String, Object> params);
    //商户端订单列表
    PageData<MerchantOrderDTO> listMerchantPage(Map<String, Object> params);
    Result updateByCheck(Long id);

    Result updateByCancel(Map<String, Object> params);

    Result updateByApplyRefund(Map<String, Object> params);

    Map<String, String> getNotify(Constants.PayMode alipay, BigDecimal bigDecimal, String out_trade_no);


    /**
     * 优惠卷
     * @param dct
     * @return
     */
    DesignConditionsDTO calculateCoupon(DesignConditionsDTO dct);

    /**
     * 计算赠送金
     * @param dct
     * @return
     */
    DesignConditionsDTO calculateGift(DesignConditionsDTO dct);

    /**
     * 优惠卷与赠送金
     * @param dct
     * @return
     */
    DesignConditionsDTO calculateGiftCoupon(DesignConditionsDTO dct);
}