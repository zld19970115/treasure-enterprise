package io.treasure.dao;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.SlaveOrderEntity;

import io.treasure.common.dao.BaseDao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;

import java.math.BigDecimal;

/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Mapper
public interface SlaveOrderDao extends BaseDao<SlaveOrderEntity> {

    void updateRefundId(String refundId,String orderId,Long goodId);

    SlaveOrderDTO getAllGoods(String orderId, long goodId);

    void updateSlaveOrderStatus(@Param("status") int status,@Param("orderId") String orderId,@Param("goodId") Long goodId);
}