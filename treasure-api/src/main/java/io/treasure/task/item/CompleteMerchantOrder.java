package io.treasure.task.item;

import io.treasure.dao.ScheduleMasterOrderMapper;
import io.treasure.service.impl.DistributionRewardServiceImpl;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class CompleteMerchantOrder implements ICompleteMerchantOrder{

    @Autowired(required = false)
    ScheduleMasterOrderMapper scheduleMasterOrderMapper;
    @Autowired
    DistributionRewardServiceImpl distributionRewardService;

    @Override
    public void CompleteParamsSetting() {

    }


    //每日凌晨5点进行清台操作
    /**     因为清掉的是此前24小时的订单有可能会带来一个问题就是前几个小时的订单线下未完成的，被清掉了      */
    @Override
    public void Clear() {
        if(!TimeUtil.isOnHour()){
            return;
        }
        String[] specifyDateTime=null;
        try{
            specifyDateTime = TimeUtil.getFinishedUpdateTime();
        }catch (ParseException e){
            System.out.println("==========清台： 指定时间格式转换异常！！！=============");
        }
        if(specifyDateTime == null)
            return;
        /*
        ctSlaveOrderMapper.updateFinished(specifyDateTime[0],specifyDateTime[1]);
        ctMerchantRoomParamsSetMapper.updateFreeRoom(specifyDateTime[0],specifyDateTime[1]);
        ctMasterOrderMapper.updateFinished(specifyDateTime[0],specifyDateTime[1]);
         */
    }

}
