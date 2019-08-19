package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.entity.MerchantOrderEntity;

import java.util.Date;

/**
 * 商户订单管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
public interface MerchantOrderService extends CrudService<MerchantOrderEntity, MerchantOrderDTO> {
    //删除订单
    void remove(long id,int status);
    //同意退款
    void updateStatusAndReason(long id, int status, String verify, Date verifyDate);
}