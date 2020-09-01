package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.*;
import io.treasure.dto.MerchantDTO;
import io.treasure.enm.EOrderRewardWithdrawRecord;
import io.treasure.entity.*;
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
import java.util.*;

import static io.treasure.enm.EOrderRewardWithdrawRecord.NEW_RECORD;

@Service
public class OrderRewardWithdrawRecordServiceImpl implements OrderRewardWithdrawRecordService {


    @Autowired(required = false)
    private MasterOrderDao masterOrderDao;
    @Autowired(required = false)
    private OrderRewardWithdrawRecordDao orderRewardWithdrawRecordDao;
    @Autowired(required = false)
    private MerchantSalesRewardRecordDao merchantSalesRewardRecordDao;
    @Autowired(required = false)
    private MerchantSalesRewardDao merchantSalesRewardDao;
    @Autowired(required = false)
    private MerchantDao merchantDao;

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
                System.out.println("记录已存在，不能插入此记录,请及时处理:"+orderId);
                return true;
            }
        }catch (Exception e){
            System.out.println("记录插入异常,请及时处理:"+orderId);
            return false;//记录更新失败
        }
        return true;
    }

    public boolean isExistByOrderId(String orderId){
        QueryWrapper<OrderRewardWithdrawRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);

        List<OrderRewardWithdrawRecordEntity> orderRewardWithdrawRecordEntities = orderRewardWithdrawRecordDao.selectList(queryWrapper);

        if(orderRewardWithdrawRecordEntities.size()>0)
            return true;
        return false;
    }

    //定时任务：每个月一号或星期一或第7天
    public void execCommission() throws ParseException {

        List<MerchantDTO> merchantDTOS = merchantDao.selectCommissionList();
        for(int i=0;i<merchantDTOS.size();i++){
            updateMerchantSalesRewardRecord(merchantDTOS.get(i));
        }
    }
    //2-3   ==========================================================================
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean updateMerchantSalesRewardRecord(MerchantDTO merchantDTO) throws ParseException {

        MerchantSalesRewardEntity sysParams = merchantSalesRewardDao.selectById(1);

        Map<String,Date> map = TimeUtil.getCommissionTimeRange(sysParams,merchantDTO.getCreateDate());

        List<MerchantSalesRewardRecordEntity> entities
                = orderRewardWithdrawRecordDao.selectCommissionListByMid(merchantDTO.getId(),map.get("startTime"),map.get("stopTime"));

        List<Long> ids = new ArrayList<>();

        for(int i=0;i<entities.size();i++){
            MerchantSalesRewardRecordEntity recordItem = entities.get(i);
            ids.add(recordItem.getId());
            //插入新记录
            //recordItem.setStartPmt(map.get("startTime"));
            recordItem.setStopPmt(map.get("stopTime"));
            try{
                merchantSalesRewardRecordDao.insert(recordItem);
            }catch (Exception e){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;//order状态更新失败
            }

        }

        int usedCode = EOrderRewardWithdrawRecord.USED_RECORD.getCode();
        try{
            if(ids.size()>0){
                orderRewardWithdrawRecordDao.updateUsedStatus(usedCode,ids);
            }
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;//order状态更新失败
        }
        return true;
    }

    /**
     * 非预计内，防止有接口调用需求，更新记录录为已读状怘(1-2更新记录状态)
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
