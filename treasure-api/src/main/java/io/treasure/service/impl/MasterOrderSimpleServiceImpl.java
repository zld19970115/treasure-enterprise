package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.utils.Result;
import io.treasure.dao.MasterOrderSimpleDao;

import io.treasure.entity.OrderSimpleEntity;
import io.treasure.service.MasterOrderSimpleService;
import io.treasure.vo.OrderPagePlus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterOrderSimpleServiceImpl implements MasterOrderSimpleService {

    @Autowired(required = false)
    private MasterOrderSimpleDao masterOrderSimpleDao;

    @Override
    public List<OrderSimpleEntity> getOrderList(Long merchantId, Integer index, Integer pageNumber){

        List<OrderSimpleEntity> orderSimpleEntities = masterOrderSimpleDao.queryByOrder(merchantId, index, pageNumber);
        for(int i=0;i<orderSimpleEntities.size();i++){
            if(orderSimpleEntities.get(i).getGoodId().equals("good_id"))
            orderSimpleEntities.set(i,orderSimpleEntities.get(i).resetGoodId());
        }
        return orderSimpleEntities;

    }

    public Integer getCount(Long merchantId){
        return masterOrderSimpleDao.qureyCountByMerchantId(merchantId);
    }

}
