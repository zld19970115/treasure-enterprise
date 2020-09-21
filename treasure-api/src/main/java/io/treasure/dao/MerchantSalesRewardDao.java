package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantSalesRewardEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantSalesRewardDao extends BaseDao<MerchantSalesRewardEntity> {
    MerchantSalesRewardEntity selectByMerchantTypeId(Integer commissionType);
}
