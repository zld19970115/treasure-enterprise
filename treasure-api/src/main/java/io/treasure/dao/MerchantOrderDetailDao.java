package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantOrderDetailEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 商户订单明细管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Mapper
public interface MerchantOrderDetailDao extends BaseDao<MerchantOrderDetailEntity> {
    /**
     * 删除
     */
    void updateStatus(long order_id,int status);
    /**
     * 根据订单号，查询订单明细
     */
    List<MerchantOrderDetailEntity> getByOrderId(long orderId,int status);
}