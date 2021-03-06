package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dao.SlaveOrderDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.SlaveOrderEntity;

import io.treasure.common.service.CrudService;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface SlaveOrderService extends CrudService<SlaveOrderEntity, SlaveOrderDTO> {

    List<SlaveOrderEntity> selectByOrderId(String orderId);
    List<SlaveOrderEntity> selectByOrderIdAndStatus(String orderId);
    PageData<SlaveOrderDTO> getOandPoGood(Map<String, Object> params);
    List<SlaveOrderEntity> selectslaveOrderByOrderId(String orderId);
    /**
     * 通过订单ID和商品ID查询此商品信息
     * @param orderId
     * @param goodId
     * @return
     */
    SlaveOrderDTO getAllGoods(String orderId, long goodId);

    /**
     * 跟新订单菜品表中的退款ID
     * @param refundId
     * @param orderId
     * @param goodId
     */
    void updateRefundId(@Param("refundId") String refundId, @Param("orderId") String orderId, @Param("goodId") Long goodId);

    void updateSlaveOrderStatus(@Param("status") int status, @Param("orderId") String orderId, @Param("goodId") Long goodId);


    Result refundGood(SlaveOrderDTO slaveOrderDTO);

    List<SlaveOrderEntity> getOrderGoods(String orderId);
    List<SlaveOrderEntity> getOrderGoods1(String orderId);

    void updateSlaveOrderPointDeduction(BigDecimal mp, BigDecimal pb, String orderId, Long goodId);

    int updateRefundReason(String refundReason, String orderId, Long goodId);

    int selectCountOfNoPayOrderByOrderId(String orderId);

    BigDecimal getDiscountsMoneyByOrderId(String orderId);

    BigDecimal getTotalFreeGoldByMasterOrderId(String orderId);

    void updateStatusByOrderId(String orderId, int conditionStatus, int newStatus);

}