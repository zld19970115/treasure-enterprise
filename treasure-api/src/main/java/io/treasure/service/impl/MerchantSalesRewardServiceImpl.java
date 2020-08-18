package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.treasure.dao.MerchantSalesRewardDao;
import io.treasure.dao.MerchantSalesRewardRecordDao;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.service.MerchantSalesRewardService;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import io.treasure.vo.RewardMchList;
import lombok.Data;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MerchantSalesRewardServiceImpl implements MerchantSalesRewardService {

    @Autowired(required = false)
    private MerchantSalesRewardDao merchantSalesRewardDao;
    @Autowired(required = false)
    private MerchantSalesRewardRecordDao merchantSalesRewardRecordDao;

    @Override
    public MerchantSalesRewardEntity getParams(){
        List<MerchantSalesRewardEntity> merchantSalesRewardEntities = merchantSalesRewardDao.selectList(null);
        if(merchantSalesRewardEntities.size()>0)
            return  merchantSalesRewardEntities.get(0);
        return null;
    }

    @Override
    public void setParams(MerchantSalesRewardEntity entity){
        merchantSalesRewardDao.updateById(entity);
    }

    @Override
    public void insertOne(MerchantSalesRewardEntity entity) {
        if (getParams() == null) {
            merchantSalesRewardDao.insert(entity);
        }
    }

    @Override
    public IPage<MerchantSalesRewardRecordEntity> getRecords(MerchantSalesRewardRecordVo vo){
        QueryWrapper<MerchantSalesRewardRecordEntity> queryWrapper = new QueryWrapper<>();
        MerchantSalesRewardRecordEntity queryEntity = vo.getMerchantSalesRewardRecordEntity();
        if(queryEntity != null){

            if(queryEntity.getId() != null)
                queryWrapper.eq("id",queryEntity.getId());
            if(queryEntity.getMId() != null)
                queryWrapper.eq("m_id",queryEntity.getMId());
            if(queryEntity.getOutline() != null)
                queryWrapper.like("outline",queryEntity.getOutline());
            if(queryEntity.getRewardType() != null)
                queryWrapper.eq("reward_type",queryEntity.getRewardType());
            if(queryEntity.getRewardValue() != null && vo.getMinValue()==null)
                queryWrapper.eq("reward_value",queryEntity.getRewardValue());//大于等于
        }

        if(vo.getMinValue() != null)
            queryWrapper.le("reward_value",vo.getMinValue());//须测
        if(vo.getStartTime() != null)
            queryWrapper.ge("create_pmt",vo.getStartTime());
        if(vo.getStopTime() != null)
            queryWrapper.le("create_pmt",vo.getStopTime());

        Page<MerchantSalesRewardRecordEntity> map = new Page<MerchantSalesRewardRecordEntity>(vo.getIndex(),vo.getPagesNum());
        IPage<MerchantSalesRewardRecordEntity> pages = merchantSalesRewardRecordDao.selectPage(map,queryWrapper);
        return pages;
    }

    public List<RewardMchList> getRewardMchList(MerchantSalesRewardRecordVo vo){

        List<RewardMchList> list = merchantSalesRewardRecordDao.reward_mch_list(vo);
        return list;
    }

    @Override
    public void delRecord(Long id){
        merchantSalesRewardRecordDao.deleteById(id);
    }

    @Override
    public void insertRecord(MerchantSalesRewardRecordEntity entity){
        merchantSalesRewardRecordDao.insert(entity);
    }

    //此功能目前不完整，暂时先不要用
    @Override
    public void insertBatchRecords(List<RewardMchList> list){
        //List<MerchantSalesRewardRecordEntity> queryList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            MerchantSalesRewardRecordEntity targetItem = new MerchantSalesRewardRecordEntity();
            RewardMchList rewardMchList = list.get(0);

            targetItem.setMId(rewardMchList.getMerchantId());
            String salesVolume = rewardMchList.getSalesVolume();
            String s = new BigDecimal(salesVolume).setScale(0, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            targetItem.setPreferenceVolume(Integer.parseInt(s));

            String merchantProceed = new BigDecimal(rewardMchList.getMerchantProceed()).setScale(0, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            targetItem.setRewardValue(Integer.parseInt(merchantProceed));

            targetItem.setCreatePmt(new Date());
            targetItem.setOutline("销售返利");
            targetItem.setRewardType(1);
            //queryList.add(targetItem);
            merchantSalesRewardRecordDao.insert(targetItem);
        }
    }

    public int updateWithDrawStatusById(List<MerchantSalesRewardRecordEntity> entites,Integer method){
        for(int i=0;i<entites.size();i++){
            MerchantSalesRewardRecordEntity entity = entites.get(i);
            entity.setMethod(method);
            entity.setCashOutStatus(2);
            merchantSalesRewardRecordDao.updateById(entity);
        }
        return 1;
    }

}
