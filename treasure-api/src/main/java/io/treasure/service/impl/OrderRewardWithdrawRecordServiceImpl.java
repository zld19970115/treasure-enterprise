package io.treasure.service.impl;


import io.treasure.dao.OrderRewardWithdrawRecordDao;
import io.treasure.enm.EOrderRewardWithdrawRecord;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.OrderRewardWithdrawRecordEntity;
import io.treasure.service.OrderRewardWithdrawRecordService;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static io.treasure.enm.EOrderRewardWithdrawRecord.NOT_COPY;

@Service
public class OrderRewardWithdrawRecordServiceImpl implements OrderRewardWithdrawRecordService {

    @Autowired(required = false)
    private OrderRewardWithdrawRecordDao orderRewardWithdrawRecordDao;


    /**
     * 系统清台时，自动将新订单记录增加至商家反佣记录内
     * @param entity
     * @return
     */
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
            orwrEntity.setStatus(NOT_COPY.getCode());                //记录未动

            orwrEntity.setOrderId(orderId);
            orwrEntity.setMId(merchantId);
            orwrEntity.setTotalPrice(totalMoney);
            orwrEntity.setActualPayment(payMoney);
            orwrEntity.setPayCoins(payCoins);
            orwrEntity.setPlatformIncome(platformBrokerage);
            orwrEntity.setCreatePmt(entity.getEatTime());           //用餐时间作为返佣参考时间

            orderRewardWithdrawRecordDao.insert(orwrEntity);
            return  true;

        }catch (Exception e){

            return false;
        }


    }

    /**
     * 更新记录录为已读状怘
     * @param ids
     * @return
     */

    @Override
    public boolean updateCopiedStatus(List<Long> ids){
        int code = EOrderRewardWithdrawRecord.COPYED.getCode();
        try{
            orderRewardWithdrawRecordDao.updateByIds(ids,code);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    public List<OrderRewardWithdrawRecordEntity> getSpecifyMerchantList(MerchantSalesRewardRecordVo vo){

        List<OrderRewardWithdrawRecordEntity> resultList = orderRewardWithdrawRecordDao.getMerchantRewardList(vo);

//ipipippop[o[ppo[o[p[pooiuytyuiop[]poiufjkllkjhghjkl
        /*
        Date lastMonthDate = TimeUtil.getLastMonthDate();

        Date monthStart = TimeUtil.getMonthStart(lastMonthDate);
        Date monthEnd = TimeUtil.getMonthEnd(lastMonthDate);

        vo.setStartTime(monthStart);
        vo.setStopTime(monthEnd);

        List<RewardMchList> list = merchantSalesRewardRecordDao.reward_mch_list(vo);
//        for(int i=0;i<list.size();i++){
//            RewardMchList rewardMchList = list.get(i);
//            rewardMchList.setDtime(new Date());
//            list.set(i,rewardMchList);
//        }
*/
        return resultList;


    }

}
