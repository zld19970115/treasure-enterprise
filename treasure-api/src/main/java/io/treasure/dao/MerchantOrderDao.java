package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantOrderEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * 商户订单管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-09
 */
@Mapper
public interface MerchantOrderDao extends BaseDao<MerchantOrderEntity> {
    void updateStatus(long id,int status);
    void updateStatusAndReason(long id, int status, String verify, Date verifyDate);
}