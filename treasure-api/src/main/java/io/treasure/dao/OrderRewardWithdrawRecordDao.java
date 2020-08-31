package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.entity.OrderRewardWithdrawRecordEntity;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface OrderRewardWithdrawRecordDao extends BaseDao<OrderRewardWithdrawRecordEntity> {

    void updateByIds(@Param("ids") List<Long> ids,
                     @Param("status") Integer status);

    void updateUsedStatus(@Param("status")Integer status,
                          @Param("stopTime")Date stopTime);

    List<MerchantSalesRewardRecordEntity> generateSalesRewardRecord(MerchantSalesRewardRecordVo vo);

    List<MerchantSalesRewardRecordEntity> selectCommissionList();

    List<MerchantSalesRewardRecordEntity> selectCommissionListByMid(@Param("mId")Long mId,
                                                                    @Param("sDate")Date sDate,
                                                                    @Param("eDate")Date eDate);
}

