package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.MerchantSalesRewardRecordDao;
import io.treasure.dao.OrderRewardWithdrawRecordDao;
import io.treasure.enm.EOrderRewardWithdrawRecord;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantSalesRewardEntity;
import io.treasure.entity.MerchantSalesRewardRecordEntity;
import io.treasure.entity.OrderRewardWithdrawRecordEntity;
import io.treasure.service.OrderRewardWithdrawRecordService;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import io.treasure.vo.RewardMchList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.treasure.enm.EOrderRewardWithdrawRecord.NEW_RECORD;

@Service
public class OrderRewardWithdrawRecordServiceImpl implements OrderRewardWithdrawRecordService {


    @Autowired(required = false)
    private MasterOrderDao masterOrderDao;

    @Autowired(required = false)
    private OrderRewardWithdrawRecordDao orderRewardWithdrawRecordDao;

    @Autowired(required = false)
    MerchantSalesRewardRecordDao merchantSalesRewardRecordDao;



    /**
     * 系统清台时，自动将新订单记录增加至商家反佣记录内(1-1更新记录列表)
     * @param entity
     * @return
     */
    @Override
    public boolean addRecord(MasterOrderEntity entity){

        BigDecimal platformIncome = entity.getPlatformBrokerage();
        if(platformIncome.doubleValue() <= 0)
            return false;

        String orderId = entity.getOrderId();
        if(isExistByOrderId(orderId))
            return false;


        Long merchantId = entity.getMerchantId();
        BigDecimal totalMoney = entity.getTotalMoney();
        BigDecimal payMoney = entity.getPayMoney();
        BigDecimal payCoins = entity.getPayCoins();

        OrderRewardWithdrawRecordEntity orwrEntity = new OrderRewardWithdrawRecordEntity();

        orwrEntity.setOrderId(orderId);
        orwrEntity.setMId(merchantId);
        orwrEntity.setTotalPrice(totalMoney);
        orwrEntity.setActualPayment(payMoney);
        orwrEntity.setPayCoins(payCoins);
        orwrEntity.setPlatformIncome(platformIncome);
        orwrEntity.setEatTime(entity.getEatTime());     //用餐时间作为返佣参考时间
        orwrEntity.setIsUsed(NEW_RECORD.getCode());     //只针对新记录
        try{
            if(!isExistByOrderId(orderId)){
                orderRewardWithdrawRecordDao.insert(orwrEntity);
            }else{
                return true;
            }
        }catch (Exception e){
            return false;//记录更新失败
        }
        return true;
    }

    public boolean isExistByOrderId(String orderId){
        QueryWrapper<MasterOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);

        List<MasterOrderEntity> masterOrderEntities = masterOrderDao.selectList(queryWrapper);

        if(masterOrderEntities.size()>0)
            return true;
        return false;
    }

    //2-1   定时汇总记录,汇总并生成奖励记录====================================================================================
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public List<MerchantSalesRewardRecordEntity> generateSalesRewardRecord(MerchantSalesRewardEntity sysParams, MerchantSalesRewardRecordEntity entity) throws ParseException {

        MerchantSalesRewardRecordVo vo = new MerchantSalesRewardRecordVo();
        vo.setMerchantSalesRewardRecordEntity(entity);
        Map<String, Date> commissionTimeRange = TimeUtil.getCommissionTimeRange(sysParams);
        vo.setStartTime(commissionTimeRange.get("startTime"));
        vo.setStopTime(commissionTimeRange.get("stopTime"));
        vo.setMinValue(sysParams.getMinimumSales().doubleValue());
        vo.setRanking(sysParams.getTradeNum());

        List<MerchantSalesRewardRecordEntity> resultList = orderRewardWithdrawRecordDao.generateSalesRewardRecord(vo);

        try{
            for(int i=0;i<resultList.size();i++){
                merchantSalesRewardRecordDao.insert(resultList.get(i));
            }
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;//记录更新异常，晚些时候再试
        }
        try{

            int code = EOrderRewardWithdrawRecord.USED_RECORD.getCode();
            orderRewardWithdrawRecordDao.updateUsedStatus(code);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;//order状态更新失败
        }

        return resultList;
    }
    //2-2   统计并拷贝==============================================================================
    //2-3   打开拷贝锁标记==========================================================================

    /**
     * 更新记录录为已读状怘(1-2更新记录状态)
     * @param ids
     * @return
     */
    @Override
    public boolean updateCopiedStatus(List<Long> ids){

        int code = EOrderRewardWithdrawRecord.USED_RECORD.getCode();
        try{
            orderRewardWithdrawRecordDao.updateByIds(ids,code);
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
