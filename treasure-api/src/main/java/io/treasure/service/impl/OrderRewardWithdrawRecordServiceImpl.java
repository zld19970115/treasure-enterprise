package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.*;
import io.treasure.dto.MerchantDTO;
import io.treasure.enm.EOrderRewardWithdrawRecord;
import io.treasure.entity.*;
import io.treasure.service.OrderRewardWithdrawRecordService;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import static java.time.OffsetDateTime.now;

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
        orwrEntity.setIsUsed(EOrderRewardWithdrawRecord.ERecordStatus.NEW_RECORD.getCode());     //只针对新记录
        try{
            if(!isExistByOrderId(orderId)){
                orderRewardWithdrawRecordDao.insert(orwrEntity);
            }else{
                System.out.println("不能插入此记录,请及时处理:"+orderId);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
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

    //更新商户可返佣记录表
    public void execCommission() throws ParseException {

        List<MerchantDTO> merchantDTOS = merchantDao.selectCommissionList();//取得需要提现的商户列表
        for(int i=0;i<merchantDTOS.size();i++){
            updateMerchantSalesRewardRecord(merchantDTOS.get(i));
        }
    }
    public int getTradeNumCondition(Long mId,EOrderRewardWithdrawRecord.EJudgeMethod jMethod, BigDecimal preferenceTotalValue){

        QueryWrapper<OrderRewardWithdrawRecordEntity> queryWrapper = new QueryWrapper<>();
        if(jMethod != null){
            queryWrapper.eq("judge_method",jMethod.getCode());
        }
        queryWrapper.gt("sales_volume",preferenceTotalValue);
        queryWrapper.orderByDesc("sales_volume");
        List<OrderRewardWithdrawRecordEntity> entities = orderRewardWithdrawRecordDao.selectList(queryWrapper);
        for(int i=0;i<entities.size();i++){
            OrderRewardWithdrawRecordEntity entity = entities.get(i);
            if(entity != null){
                if(entity.getMId() == mId){
                    return i;
                }
            }
        }
        return 0;
    }

    public int getRewardRate(MerchantSalesRewardEntity sysParams,BigDecimal value){
        Integer segmentA = sysParams.getSegmentA();

        if (segmentA == null){
            System.out.println("merchant commission:segmentA error");
            return -2;//系统参数有误
        }
        Integer segmentB = sysParams.getSegmentB()==null?0:sysParams.getSegmentB();
        Integer segmentC = sysParams.getSegmentC()==null?0:sysParams.getSegmentC();
        Integer segmentD = sysParams.getSegmentD()==null?0:sysParams.getSegmentD();
        Integer segmentE = sysParams.getSegmentE()==null?0:sysParams.getSegmentE();

        if(value.doubleValue() >= segmentA){
            if(sysParams.getRateA() != null)
                return sysParams.getRateA();
            System.out.println("merchant commission:rateA error");
            return -2;
        }else if(value.doubleValue() >= segmentB){
            if(segmentB != 0){
                if(sysParams.getRateB() != null)
                    return sysParams.getRateB();
                System.out.println("merchant commission:rateB error");
                return -2;
            }
            return -1;
        }else if(value.doubleValue() >= segmentC){
            if(segmentC != 0){
                if(sysParams.getRateC() != null)
                    return sysParams.getRateC();
                System.out.println("merchant commission:rateC error");
                return -2;
            }
            return -1;
        }else if(value.doubleValue() >= segmentD){
            if(segmentD != 0) {
                if(sysParams.getRateD() != null)
                    return sysParams.getRateD();
                System.out.println("merchant commission:rateD error");
                return -2;
            }
            return -1;
        }else if(value.doubleValue() >= segmentE){
            if(segmentE != 0){
                if(sysParams.getRateE() != null)
                    return sysParams.getRateE();
                System.out.println("merchant commission:rateE error");
                return -2;
            }
            return -1;
        }
        return -1;
    }

    /**
     *
     * @param sysParams
     * @param merchantDTO
     * @param preferenceTotalValue 商家销量?还是平台扣点值
     * @return
     */
    public BigDecimal calculateCommissionValue(MerchantSalesRewardEntity sysParams,MerchantDTO merchantDTO,BigDecimal preferenceTotalValue){
        BigDecimal result = new BigDecimal("0");
        Integer judgeMethod = sysParams.getJudgeMethod();
        EOrderRewardWithdrawRecord.EJudgeMethod jMethod = EOrderRewardWithdrawRecord.EJudgeMethod.fromCode(judgeMethod);
        int rewardRate = 0;
        switch (jMethod){
            case TRADE_NUM_MODE://名次
                Integer res = sysParams.getMinimumSales();//销量需要大于或等于此值
                BigDecimal minimumSales = new BigDecimal(res+"");
                //minimumSales = minimumSales.multiply(new BigDecimal("0.15")).setScale(2,BigDecimal.ROUND_DOWN);
                if(preferenceTotalValue.doubleValue()<minimumSales.doubleValue())
                    return result;
                //取得用户的名次
                int tradeNum = getTradeNumCondition(merchantDTO.getId(),jMethod,preferenceTotalValue);
                rewardRate = getRewardRate(sysParams, new BigDecimal(tradeNum+""));
                break;
            case SALES_VOLUME_MODE://销量
                rewardRate = getRewardRate(sysParams, preferenceTotalValue);
                break;
            case FIXED_VALUE_MODE://固定值
                Integer res1 = sysParams.getMinimumSales();//销量需要大于或等于此值
                BigDecimal minimumSales1 = new BigDecimal(res1+"");
                if(preferenceTotalValue.doubleValue()<minimumSales1.doubleValue())
                    return result;
                rewardRate = sysParams.getRateA();
                break;
        }
        //计算具体返佣金额
        if(rewardRate >0){
            BigDecimal tmpRate = new BigDecimal(rewardRate + "");
            BigDecimal multiply = tmpRate.multiply(preferenceTotalValue);
            result = multiply.multiply(new BigDecimal("0.0001")).setScale(2,BigDecimal.ROUND_DOWN);
            return result;
        }else{
            if(rewardRate == -2)
                System.out.println("返佣系统参数设置有误，请及时处理!");
            return result;
        }
    }

    //2-3   ==========================================================================
    /**
     * 更新orderSalesRewardRecord -至->MerchantSalesRewardRecord 中
     * @param merchantDTO
     * @return
     * @throws ParseException
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean updateMerchantSalesRewardRecord(MerchantDTO merchantDTO) throws ParseException {
        Integer commissionType = merchantDTO.getCommissionType();

        //获取本商户反佣使用的参数
        MerchantSalesRewardEntity sysParams = merchantSalesRewardDao.selectByMerchantTypeId(commissionType);
        if(sysParams == null){
            System.out.println("未的找到本商家对应的返佣类型("+commissionType+"),请及时处理"+now());
            return false;
        }
        Map<String,Date> map = TimeUtil.getCommissionTimeRange(sysParams,merchantDTO.getCreateDate());
        //System.out.println(TimeUtil.simpleDateFormat.format(map.get("startTime"))+"：开始，：结束"+TimeUtil.simpleDateFormat.format(map.get("stopTime")));
        List<OrderRewardWithdrawRecordEntity> entities = orderRewardWithdrawRecordDao.selectCommissionListByMid(
                merchantDTO.getId(),map.get("startTime"),map.get("stopTime"));

        if(entities.size()==0)
            return false;

        List<Long> ids = new ArrayList<>();
        BigDecimal commissionVolume = new BigDecimal("0");
        BigDecimal totalSales = new BigDecimal("0");
        for(int i=0;i<entities.size();i++){
            BigDecimal platformIncome = entities.get(i).getPlatformIncome();
            BigDecimal totalPrice = entities.get(i).getTotalPrice();
            commissionVolume = commissionVolume.add(platformIncome);
            totalSales = totalSales.add(totalPrice);
            ids.add(entities.get(i).getId());
        }
        //计算此商家可返的佣金值
        BigDecimal awards = calculateCommissionValue(sysParams,merchantDTO,commissionVolume);
        if(awards.doubleValue() <= 0){
            System.out.println(TimeUtil.simpleDateFormat.format(new Date())+"===>"+merchantDTO.getId()+"不具备返佣条件!");
            return false;
        }
        MerchantSalesRewardRecordEntity merchantSales = new MerchantSalesRewardRecordEntity();
        merchantSales.setMId(merchantDTO.getId());
        merchantSales.setMethod(0);
        merchantSales.setCashOutStatus(1);
        merchantSales.setAuditStatus(0);
        merchantSales.setSalesVolume(totalSales);
        merchantSales.setAuditComment("汇总提取");
        merchantSales.setCommissionVolume(awards);
        merchantSales.setStopPmt(map.get("stopTime"));

        Long updateId = null;
        try{
            //merchantSalesRewardRecordDao.insert(merchantSales);
            merchantSalesRewardRecordDao.insertEntity(merchantSales);
            updateId = merchantSales.getId();
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;//order状态更新失败
        }

        int usedCode = EOrderRewardWithdrawRecord.ERecordStatus.USED_RECORD.getCode();
        try{
            if(ids.size()>0 && updateId != null){
                orderRewardWithdrawRecordDao.updateUsedStatus(usedCode,ids,updateId);
            }else{
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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

        int code = EOrderRewardWithdrawRecord.ERecordStatus.USED_RECORD.getCode();
        try{
            orderRewardWithdrawRecordDao.updateByIds(ids,code);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
