package io.treasure.dao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.SlaveOrderEntity;

import io.treasure.common.dao.BaseDao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Mapper
public interface SlaveOrderDao extends BaseDao<SlaveOrderEntity> {

    void updateRefundId(@Param("refundId") String refundId, @Param("orderId") String orderId, @Param("goodId") Long goodId);
    List<SlaveOrderEntity> selectslaveOrderByOrderId(String orderId);
    SlaveOrderDTO getAllGoods(@Param("orderId") String orderId, @Param("goodId") long goodId);

    void updateSlaveOrderStatus(@Param("status") int status, @Param("orderId") String orderId, @Param("goodId") Long goodId);

    void updateSlaveOrderPointDeduction(@Param("mp") BigDecimal mp, @Param("pb") BigDecimal pb, @Param("orderId") String orderId, @Param("goodId") Long goodId);

    List<SlaveOrderEntity> getOrderGoods(String orderId);
    List<SlaveOrderEntity> getOrderGoods1(String orderId);

    List<SlaveOrderDTO> getOandPoGood(Map<String, Object> params);

    int updateRefundReason(@Param("refundReason") String refundReason, @Param("orderId") String orderId, @Param("goodId") Long goodId);




    BigDecimal getDiscountsMoneyByOrderId(String orderId);

    BigDecimal selectTotalFreeGoldByMasterOrderId(String orderId);

    void updateStatusByOrderId(@Param("orderId") String orderId,
                               @Param("conditionStatus") int conditionStatus,
                               @Param("newStatus") int newStatus);

    int selectCountOfNoPayOrderByOrderId(String orderId);
}