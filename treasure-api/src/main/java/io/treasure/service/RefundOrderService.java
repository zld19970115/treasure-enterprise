package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.RefundOrderDTO;
import io.treasure.entity.RefundOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 退菜订单表
 */
public interface RefundOrderService  extends CrudService<RefundOrderEntity, RefundOrderDTO> {

    void insertRefundOrder(RefundOrderEntity refundOrderDTO);

    void updateRefundId(String refundId, String orderId,Long goodId);

    PageData<RefundOrderEntity> getRefundOrderByMerchantId(Map<String, Object> params);

    void updateDispose( int dispose,String orderId,Long goodId);

    void updateMasterOrderPayMoney(String orderId,Long goodId);
}
