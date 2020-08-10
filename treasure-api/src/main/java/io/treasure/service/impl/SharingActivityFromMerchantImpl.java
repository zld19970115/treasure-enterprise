package io.treasure.service.impl;

import io.treasure.dao.MerchantDao;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingActivityExtendsEntity;
import io.treasure.service.SharingActivityFromMerchant;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SharingActivityFromMerchantImpl implements SharingActivityFromMerchant {

    @Autowired(required = false)
    private MerchantDao merchantDao;

    @Override
    public boolean proposerIsMch(SharingActivityEntity entity){

        Long creator = entity.getCreator();

        Map<String,Object> ids = new HashMap<>();
        ids.put("item",creator);
        List<MerchantDTO> merchantDTOS = merchantDao.selectByMartId(ids);

        if(merchantDTOS == null)
            return false;//并将非商家助力活动

        Long rewardId = entity.getRewardId();
        Long rewardMchId = entity.getRewardMchId();
        if(entity.getRewardId() != rewardId || entity.getRewardMchId() != rewardMchId){
            return false;//参数有误，非法操作
        }
        return true;
    }

}
