package io.treasure.dao;
import io.treasure.entity.MasterOrderEntity;

import io.treasure.common.dao.BaseDao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Mapper
public interface MasterOrderDao extends BaseDao<MasterOrderEntity> {
    //refundReason
    void updateStatusAndReason(long id, int status, long updater, Date refundDate, String refundReason);

    MasterOrderEntity selectByOrderId(String orderId);
}