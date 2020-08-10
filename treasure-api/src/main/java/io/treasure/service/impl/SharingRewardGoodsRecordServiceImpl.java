package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.SharingRewardGoodsRecordDao;
import io.treasure.entity.SharingRewardGoodsRecordEntity;
import io.treasure.service.SharingRewardGoodsRecordService;
import io.treasure.vo.SharingRewardGoodsRecordComboVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SharingRewardGoodsRecordServiceImpl implements SharingRewardGoodsRecordService {

    @Autowired(required = false)
    private SharingRewardGoodsRecordDao sharingRewardGoodsRecordDao;


    @Override
    public void insertItem(SharingRewardGoodsRecordEntity expectantEntity) {
        sharingRewardGoodsRecordDao.insert(expectantEntity);
    }

    @Override
    public void updateItemById(SharingRewardGoodsRecordEntity updateEntity) {
        sharingRewardGoodsRecordDao.updateById(updateEntity);
    }

    @Override
    public List<SharingRewardGoodsRecordEntity> getRecords(SharingRewardGoodsRecordComboVo conditionEntity) {
        SharingRewardGoodsRecordEntity srgr = conditionEntity.getSharingRewardGoodsRecordEntity();
        Long csrId = srgr.getCsrId();
        Long clientId = srgr.getClientId();
        Integer status = srgr.getStatus();
        Integer activityId = srgr.getActivityId();
        Long merchantId = srgr.getMerchantId();
        Long goodsId = srgr.getGoodsId();
        Integer expireFlag = conditionEntity.getExpireFlag();

        QueryWrapper<SharingRewardGoodsRecordEntity> queryWrapper = new QueryWrapper<>();
        if(csrId != null)
            queryWrapper.eq("csr_id",csrId);
        if(clientId != null)
            queryWrapper.eq("client_id",clientId);
        if(status != null)
            queryWrapper.eq("status",status);
        if(activityId != null)
            queryWrapper.eq("activity_id",activityId);
        if(merchantId != null)
            queryWrapper.eq("merchant_id",merchantId);
        if(goodsId != null)
            queryWrapper.eq("goods_id",goodsId);
        if(expireFlag == 1){//仅有效
            queryWrapper.le("expire_time",new Date());
        }else if(expireFlag == 2){//仅失效
            queryWrapper.gt("expire_time",new Date());
        }
        return sharingRewardGoodsRecordDao.selectList(queryWrapper);
    }



}
