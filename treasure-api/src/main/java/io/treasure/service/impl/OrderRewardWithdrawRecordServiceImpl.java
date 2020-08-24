package io.treasure.service.impl;


import io.treasure.dao.OrderRewardWithdrawRecordDao;
import io.treasure.enm.EOrderRewardWithdrawRecord;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.OrderRewardWithdrawRecordEntity;
import io.treasure.service.OrderRewardWithdrawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static io.treasure.enm.EOrderRewardWithdrawRecord.NOT_WITHDRAW;

@Service
public class OrderRewardWithdrawRecordServiceImpl implements OrderRewardWithdrawRecordService {

    @Autowired(required = false)
    private OrderRewardWithdrawRecordDao orderRewardWithdrawRecordDao;

    //添加新记录，本记录由清台服务维护
    @Override
    public boolean addPreWithdrawRecord(MasterOrderEntity entity){

        try{
            BigDecimal platformBrokerage = entity.getPlatformBrokerage();
            if(platformBrokerage.doubleValue() <= 0)
                return true;

            String orderId = entity.getOrderId();
            Long merchantId = entity.getMerchantId();
            BigDecimal totalMoney = entity.getTotalMoney();
            BigDecimal payMoney = entity.getPayMoney();
            BigDecimal payCoins = entity.getPayCoins();

            OrderRewardWithdrawRecordEntity orwrEntity = new OrderRewardWithdrawRecordEntity();
            orwrEntity.setStatus(NOT_WITHDRAW.getCode());                //未提现状态

            orwrEntity.setOrderId(orderId);
            orwrEntity.setMId(merchantId);
            orwrEntity.setTotalPrice(totalMoney);
            orwrEntity.setActualPayment(payMoney);
            orwrEntity.setPayCoins(payCoins);
            orwrEntity.setPlatformIncome(platformBrokerage);
            orwrEntity.setCreatePmt(new Date());

            orderRewardWithdrawRecordDao.insert(orwrEntity);
            return  true;

        }catch (Exception e){

            return false;
        }


    }

    @Override
    public boolean updateWithdrawFlagById(List<Long> ids, EOrderRewardWithdrawRecord eStatus){

        try{
            orderRewardWithdrawRecordDao.updateByIds(ids,eStatus.getCode());
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
