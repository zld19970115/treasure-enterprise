package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.entity.OrderRewardWithdrawRecordEntity;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRewardWithdrawRecordDao extends BaseDao<OrderRewardWithdrawRecordEntity> {

    void updateByIds(@Param("ids") List<Long> ids,
                     @Param("status") Integer status);
    void updateUsedStatus(Integer status);


    List<MerchantSalesRewardRecordEntity> generateSalesRewardRecord(MerchantSalesRewardRecordVo vo);
}

