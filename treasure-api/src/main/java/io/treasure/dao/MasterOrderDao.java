package io.treasure.dao;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.MerchantOrderDTO;
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
    //商户端查询
    List<MerchantOrderDTO> listMerchant(Map params);
    MasterOrderEntity selectByOrderId(String orderId);
      List<MasterOrderEntity>  selectPOrderId(String orderId);
    MasterOrderDTO getOrderByOrderId(String orderId);
    void updateOrderStatus(@Param("status") int status,@Param("orderId") String orderId);
    void updatePayMode(@Param("payMode") String payMode,@Param("orderId") String orderId);
    MasterOrderDTO getOrderById(long id);
    void updatePayMoney(@Param("money") BigDecimal PayMoney,@Param("orderId") String orderId);
}