package io.treasure.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import io.treasure.vo.RewardMchList;

import java.text.ParseException;
import java.util.List;

public interface MerchantSalesRewardService {

    MerchantSalesRewardEntity getParams();
    void setParams(MerchantSalesRewardEntity entity);

    void insertOne(MerchantSalesRewardEntity entity);
    IPage<MerchantSalesRewardRecordEntity> getRecords(MerchantSalesRewardRecordVo vo);
    void delRecord(Long id);
    void insertRecord(MerchantSalesRewardRecordEntity entity);
    void insertBatchRecords(List<RewardMchList> list);
    List<RewardMchList> getRewardMchList(MerchantSalesRewardRecordVo vo) throws ParseException;
    List<RewardMchList> getRewardMchList(List<Long> mIds);
    int updateWithDrawStatusById(List<MerchantSalesRewardRecordEntity> entites,Integer method);
    String getNotWithdrawRewardAmount(Long mchId);

}
