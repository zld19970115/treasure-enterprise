package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantOrderDetailDTO;
import io.treasure.entity.MerchantOrderDetailEntity;

/**
 * 商户订单明细管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
public interface MerchantOrderDetailService extends CrudService<MerchantOrderDetailEntity, MerchantOrderDetailDTO> {
    //删除订单
    void remove(long orderId,int status);
}