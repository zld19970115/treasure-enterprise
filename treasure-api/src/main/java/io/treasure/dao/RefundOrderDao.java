package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.RefundOrderDTO;
import io.treasure.entity.RefundOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 退款订单表
 */
@Mapper
public interface RefundOrderDao extends BaseDao<RefundOrderEntity> {

    void insertRefundOrder(RefundOrderEntity refundOrderDTO);

    void updateRefundId(@Param("refundId") String refundId, @Param("orderId") String orderId, @Param("goodId") Long goodId);

    List<RefundOrderDTO> getRefundOrderByMerchantId(Map<String, Object> params);

    void updateDispose(@Param("dispose") int dispose, @Param("orderId") String orderId, @Param("goodId") Long goodId);
}
