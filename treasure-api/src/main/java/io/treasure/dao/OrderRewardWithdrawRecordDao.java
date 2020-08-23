package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.OrderRewardWithdrawRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRewardWithdrawRecordDao extends BaseDao<OrderRewardWithdrawRecordEntity> {

    void updateByIds(@Param("ids") List<Long> ids,
                     @Param("status") Integer status);


}
