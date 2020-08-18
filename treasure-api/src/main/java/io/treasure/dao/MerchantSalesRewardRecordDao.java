package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import io.treasure.vo.RewardMchList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MerchantSalesRewardRecordDao extends BaseDao<MerchantSalesRewardRecordEntity> {


    //void insertBatch(List<MerchantSalesRewardRecordEntity> list);
    List<RewardMchList> reward_mch_list(MerchantSalesRewardRecordVo vo);

}
