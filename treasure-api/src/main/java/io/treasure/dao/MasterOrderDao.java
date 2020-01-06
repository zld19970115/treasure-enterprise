package io.treasure.dao;
import io.treasure.common.page.PageData;
import io.treasure.dto.ClientUserCollectDTO;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.entity.MasterOrderEntity;

import io.treasure.common.dao.BaseDao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

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
@Mapper
public interface MasterOrderDao extends BaseDao<MasterOrderEntity> {
    //refundReason
    int updateStatusAndReason(@Param("id") long id, @Param("status") int status, @Param("updater") long updater, @Param("refundDate") Date refundDate, @Param("refundReason")  String refundReason);
    int updateStatusById(@Param("id") long id, @Param("status") int status, @Param("updater") long updater, @Param("refundDate") Date refundDate, @Param("refundReason")  String refundReason);
    //商户端查询
    List<MerchantOrderDTO> listMerchant(Map params);
    List<MerchantOrderDTO> listMerchant2(Map params);
    List<MasterOrderEntity>  selectByUserId(long userId);
    List<MasterOrderEntity> selectByMasterId(Map<String, Object> params);
    MasterOrderEntity selectByOrderId(String orderId);
      List<MasterOrderEntity>  selectPOrderId(String orderId);
    List<MasterOrderEntity>  selectBYPOrderId(String orderId);
    List<MasterOrderEntity>  selectPOrderIdByMainOrderID(String orderId);
    List<MasterOrderEntity> selectPOrderIdAndS1(String orderId);
    List<OrderDTO> selectPOrderIdAndS(String orderId);
    List<OrderDTO> selectOrder(String orderId);
    MasterOrderDTO getOrderByOrderId(String orderId);
    void updateOrderStatus(@Param("status") int status,@Param("orderId") String orderId);
    void updatePayMode(@Param("payMode") String payMode,@Param("orderId") String orderId);
    MasterOrderDTO getOrderById(long id);
    void updatePayMoney(@Param("PayMoney") BigDecimal PayMoney,@Param("orderId") String orderId);
    MasterOrderEntity getRoomOrderByPorderId(String orderId);
    List<OrderDTO> getOrderDTOByPorderId(String orderId);
    List<MasterOrderEntity> getOrderByPOrderId(String orderId);
    List<OrderDTO> getAllMainOrder(Map params);
    List<MasterOrderEntity>getAuxiliaryOrderByOrderId(String orderId);
    List<OrderDTO>getAuxiliaryOrder(Map params);
    List<MasterOrderEntity> getStatus4Order(Map<String, Object> params);
    OrderDTO getMasterOrder(String orderId);
    OrderDTO getOrder(String orderId);
    List<OrderDTO> getOrder1(String orderId);
    List<MasterOrderEntity>  selectPOrderIdHavePaid(String orderId);
    List<MasterOrderEntity>  selectAgreeRefundOrder(String orderId);
    List<OrderDTO> getPayOrder(Map params);
    List<MasterOrderEntity>getAuxiliaryPayOrder(@Param("orderId")  String orderId,@Param("status") int status);
    List<MasterOrderEntity>getAuxiliaryPayOrders(String orderId);
    List<MasterOrderEntity>getAuxiliaryPayOrderss(String orderId);
    void updateSlaveOrderPointDeduction(@Param("mp") BigDecimal mp,@Param("pb")BigDecimal pb,@Param("orderId") String orderId);
    MasterOrderEntity getOrderByReservationId(long reservationId);
    List<OrderDTO> getAffiliateOrde(String orderId);
}