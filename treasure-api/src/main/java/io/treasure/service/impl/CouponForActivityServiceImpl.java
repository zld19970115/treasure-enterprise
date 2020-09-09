package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.CouponRuleDao;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dao.MulitCouponBoundleDao;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.*;
import io.treasure.service.ClientUserService;
import io.treasure.service.CouponForActivityService;
import io.treasure.service.MasterOrderService;
import io.treasure.service.SignedRewardSpecifyTimeService;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.MerchantSalesRewardRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import static java.time.LocalDate.now;

@Service
public class CouponForActivityServiceImpl implements CouponForActivityService {

    @Autowired(required = false)
    private MulitCouponBoundleDao mulitCouponBoundleDao;
    @Autowired(required = false)
    private ClientUserDao clientUserDao;

    @Autowired
    private ClientUserService clientUserService;

    @Autowired(required = false)
    private CouponRuleDao couponRuleDao;

    @Autowired(required = false)
    MasterOrderDao masterOrderDao;

    @Autowired
    private MasterOrderService masterOrderService;

    @Autowired
    private SignedRewardSpecifyTimeService signedRewardSpecifyTimeService;

    @Override
    public BigDecimal getClientActivityCoinsVolume(Long clientUser_id){

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("use_status",0);
        //queryWrapper.le("got_pmt",now());
        queryWrapper.ge("expire_pmt",now());
        queryWrapper.select("sum(coupon_value - consume_value) as coupon_value");
        MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleDao.selectOne(queryWrapper);
        if(mulitCouponBoundleEntity == null)
            return new BigDecimal("0");
        return mulitCouponBoundleEntity.getCouponValue();
    }

    /**
     * 返回可以使用的宝币总值
     * @param clientUser_id
     * @return
     */
    @Override
    public BigDecimal getClientCanUseTotalCoinsVolume(Long clientUser_id){
        CouponRuleEntity couponRuleEntity = getCouponRuleEntity();
        Integer coinsLimit = couponRuleEntity.getConsumeLimit();//消费限值

        BigDecimal clientActivityCoinsVolume = getClientActivityCoinsVolume(clientUser_id);
        if(clientActivityCoinsVolume.doubleValue()>coinsLimit.doubleValue()){
            clientActivityCoinsVolume = new BigDecimal(coinsLimit);
        }
        ClientUserEntity clientUserEntity = clientUserDao.selectById(clientUser_id);
        if(clientUserEntity == null)
            return new BigDecimal("0");
        BigDecimal clientCoinsVolume = clientUserEntity.getBalance();
        clientCoinsVolume = clientCoinsVolume.add(clientActivityCoinsVolume);

        return clientCoinsVolume;
    }
    @Override
    public boolean coinsIsEnable(Long clientUser_id,BigDecimal coins){
        BigDecimal clientCanUseTotalCoinsVolume = getClientCanUseTotalCoinsVolume(clientUser_id);
        if(coins.compareTo(clientCanUseTotalCoinsVolume)>0){
            return false;
        }
        return true;
    }

    /**
     * @param clientUser_id
     * @param coins 使用宝币付款，并扣除宝币的值
     * @param orderId
     * @return
     */
    @Override
    public void updateCoinsConsumeRecord(Long clientUser_id, BigDecimal coins, String orderId){

        BigDecimal clientCanUseTotalCoinsVolume = getClientCanUseTotalCoinsVolume(clientUser_id);//可以使用的宝币总数

        CouponRuleEntity couponRuleEntity = getCouponRuleEntity();
        Integer coinsLimit = couponRuleEntity.getConsumeLimit();//消费限值

        if(coins.compareTo(clientCanUseTotalCoinsVolume)>0){
            System.out.println("并发问题：抵扣超限,将提前扣除["+ TimeUtil.simpleDateFormat.format(new Date())+":"+clientUser_id+","+clientCanUseTotalCoinsVolume+"-"+coins+"]");

            BigDecimal clientCoinsVolume = getClientActivityCoinsVolume(clientUser_id);

            ClientUserEntity clientUserEntity = clientUserDao.selectById(clientUser_id);
            BigDecimal balance = clientUserEntity.getBalance();
            clientUserEntity.setBalance(new BigDecimal("0"));
            clientUserDao.updateById(clientUserEntity);
            BigDecimal subtract = coins.subtract(balance);

            updateActivityCoinsConsumeRecord(clientUser_id,balance,orderId);

        }else{
            //正常扣除
            BigDecimal canUseActivityCoins = getClientActivityCoinsVolume(clientUser_id);
            BigDecimal coinsLimitDB = new BigDecimal(coinsLimit+"");
            if(canUseActivityCoins.compareTo(coinsLimitDB)>0){
                canUseActivityCoins = coinsLimitDB;         //宝币限额
            }

            if(canUseActivityCoins.compareTo(coins)>=0){
                updateActivityCoinsConsumeRecord(clientUser_id,coins,orderId); //只扣除活动宝币里的值
            }else{
                updateActivityCoinsConsumeRecord(clientUser_id,canUseActivityCoins,orderId);
                BigDecimal subtract = coins.subtract(canUseActivityCoins);

                ClientUserEntity clientUserEntity = clientUserDao.selectById(clientUser_id); //扣除充值宝币的钱
                BigDecimal balance = clientUserEntity.getBalance();
                BigDecimal newBalance = balance.subtract(subtract);
                if(newBalance.doubleValue()>=0d){
                    clientUserEntity.setBalance(newBalance);
                }else{
                    System.out.println("[异常宝币余额不足]:"+clientUser_id+","+newBalance);
                    clientUserEntity.setBalance(new BigDecimal("0"));
                }
                clientUserDao.updateById(clientUserEntity);
            }
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean updateActivityCoinsConsumeRecord(Long clientUser_id,BigDecimal coins,String orderId){

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("use_status",0);
        queryWrapper.ge("expire_pmt",new Date());
        queryWrapper.orderByAsc("expire_pmt");

        List<MulitCouponBoundleEntity> resourceEntities = null;
        try {
            resourceEntities = mulitCouponBoundleDao.selectList(queryWrapper);
            if(resourceEntities.size() == 0)
                return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        List<Long> maxedOuts = new ArrayList<>();
        BigDecimal surplusCoins = coins;
        Long surplusId = null;
        for(int i=0;i<resourceEntities.size();i++){
            MulitCouponBoundleEntity item = resourceEntities.get(i);

            BigDecimal subtract = item.getCouponValue().subtract(item.getConsumeValue());
            if(surplusCoins.compareTo(subtract)>0){
                surplusCoins = surplusCoins.subtract(subtract);
                maxedOuts.add(item.getId());
                //所有都扣除完毕了还差
                if(i== resourceEntities.size()-1){
                    System.out.println("【活动币扣除异常】"+clientUser_id+"还差"+subtract+"请及时处理!");
                }
                //正好扣除完毕
                if(surplusCoins.compareTo(subtract)==0){
                    i=resourceEntities.size();
                }
            }else{

                surplusId = item.getId();
                i=resourceEntities.size();
                surplusCoins = surplusCoins.add(item.getConsumeValue());

            }
        }
        try {
            //更新卡为使用且无效状态
            if (maxedOuts.size() > 0) {
                mulitCouponBoundleDao.updateStatusByIds(maxedOuts, null);
            }

            //减掉剩余的余额
            if (surplusId != null) {
                List<Long> idTmp = new ArrayList<>();
                idTmp.add(surplusId);
                BigDecimal couponValueLast = mulitCouponBoundleDao.selectById(surplusId).getCouponValue();
                if(surplusCoins.compareTo(couponValueLast)==0){
                    mulitCouponBoundleDao.updateStatusByIds(idTmp, null);
                }else{
                    mulitCouponBoundleDao.updateStatusByIds(idTmp, surplusCoins);
                }
            }

            MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(orderId);
            masterOrderEntity.setActivityCoins(coins);
            masterOrderDao.updateById(masterOrderEntity);   //记录订单扣除记录

        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public CouponRuleEntity getCouponRuleEntity(){
        return couponRuleDao.selectById(1);
    }
    @Override
    public void resumeActivityCoinsRecord(Long clientUser_id,BigDecimal coins){

        if(coins.doubleValue()==0)
            return;
        CouponRuleEntity couponRuleEntity = getCouponRuleEntity();
        Date expireTime = couponRuleEntity.getExpireTime();

        MulitCouponBoundleEntity entity = new MulitCouponBoundleEntity();
        entity.setOwnerId(clientUser_id);
        entity.setCouponValue(coins);
        entity.setType(1);
        entity.setGetMethod(4);
        entity.setUseStatus(0);
        entity.setGotPmt(new Date());
        entity.setExpirePmt(expireTime);
        mulitCouponBoundleDao.insert(entity);
    }


    /**
     * 用于退款调用
     * @param clientUser_id
     * @param orderId
     */
    @Override
    public void resumeAllCoinsRecord(Long clientUser_id,String orderId){

        MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(orderId);
        BigDecimal activityCoins = masterOrderEntity.getActivityCoins();
        BigDecimal payCoins = masterOrderEntity.getPayCoins();//，此数加到用户活动宝币中
        if(payCoins.doubleValue()<=0)
            return;
        BigDecimal userBalance = new BigDecimal("0");
        userBalance = payCoins.subtract(activityCoins);         //1,此数加到用户余额中

        //恢复用户中宝币记录
        ClientUserEntity clientUserEntity = clientUserService.selectById(clientUser_id);
        BigDecimal balance = clientUserEntity.getBalance();
        balance = balance.add(userBalance);
        clientUserEntity.setBalance(balance);
        clientUserService.updateById(clientUserEntity);

        //恢复活动中宝币记录
        if(activityCoins.doubleValue()>0)
            resumeActivityCoinsRecord(clientUser_id,activityCoins);

    }

//=====================================================================================================================

    @Override
    public SignedRewardSpecifyTimeEntity getParamsById(Long id){
        Long requireId = id==null?1:id;
        SignedRewardSpecifyTimeEntity signedParamsById = signedRewardSpecifyTimeService.getSignedParamsById(requireId);
        return signedParamsById;
    }

    @Override
    public Map<String,String> getSignedActivityCoinsNumberInfo() throws ParseException {
        Map<String,String> map = new HashMap<>();
        String value = "value";
        String count = "count";

        SignedRewardSpecifyTimeEntity signedParamsById = getParamsById(null);
        Integer person_amount = signedParamsById.getPersonAmount();
        Integer reward_value = signedParamsById.getRewardValue();
        BigDecimal sumForSignedReward = getSumForSignedReward();
        Integer personNumberForSignedReward = getPersonNumberForSignedReward();
        if(person_amount> personNumberForSignedReward && reward_value.doubleValue() > sumForSignedReward.doubleValue()){
            Integer res_count = person_amount- personNumberForSignedReward;
            map.put(count,res_count+"");
            BigDecimal res_value = new BigDecimal(reward_value).subtract(sumForSignedReward).setScale(2,BigDecimal.ROUND_DOWN);
            map.put(value,res_value+"");
            return map;
        }else{
            map.put(count,0+"");
            map.put(value,0+"");
            return map;
        }
    }

    public BigDecimal getSumForSignedReward() throws ParseException {

        SignedRewardSpecifyTimeEntity signedParamsById = getParamsById(null);
        Date start_pmt = signedParamsById.getStartPmt();
        Date ending_pmt = signedParamsById.getEndingPmt();
        Date startConvert = TimeUtil.getCurrentDateAndTime(start_pmt);
        Date endingConvert = TimeUtil.getCurrentDateAndTime(ending_pmt);

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",1);
        queryWrapper.ge("get_method",3);
        queryWrapper.ge("got_pmt",startConvert);
        queryWrapper.le("got_pmt",endingConvert);
        queryWrapper.select("sum(coupon_value) as coupon_value");
        MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleDao.selectOne(queryWrapper);
        if(mulitCouponBoundleEntity == null)
            return new BigDecimal("0");
        BigDecimal couponValue = mulitCouponBoundleEntity.getCouponValue();
        return couponValue;
    }
    public Integer getPersonNumberForSignedReward() throws ParseException {

        SignedRewardSpecifyTimeEntity signedParamsById = getParamsById(null);
        Date start_pmt = signedParamsById.getStartPmt();
        Date ending_pmt = signedParamsById.getEndingPmt();
        Date startConvert = TimeUtil.getCurrentDateAndTime(start_pmt);
        Date endingConvert = TimeUtil.getCurrentDateAndTime(ending_pmt);

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",1);
        queryWrapper.ge("get_method",3);
        queryWrapper.ge("got_pmt",startConvert);
        queryWrapper.le("got_pmt",endingConvert);

        Integer count = mulitCouponBoundleDao.selectCount(queryWrapper);
        return count;
    }

    @Override
    public Boolean clientCheckForSignedForReward(Long clientUser_id) throws ParseException {

        SignedRewardSpecifyTimeEntity signedParamsById = getParamsById(null);
        Date start_pmt = signedParamsById.getStartPmt();
        Date ending_pmt = signedParamsById.getEndingPmt();
        Date startConvert = TimeUtil.getCurrentDateAndTime(start_pmt);
        Date endingConvert = TimeUtil.getCurrentDateAndTime(ending_pmt);

        Integer timesLimit = signedParamsById.getTimes();
        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.ge("get_method",3);

        queryWrapper.ge("got_pmt",startConvert);
        queryWrapper.le("got_pmt",endingConvert);

        Integer times = mulitCouponBoundleDao.selectCount(queryWrapper);
        if(times < timesLimit)
            return true;
        return false;
    }

    @Override
    public void insertClientActivityRecord(Long clientId,BigDecimal bd,Integer method,Integer validity, ESharingRewardGoods.ActityValidityUnit actityValidityUnit){
        Integer maxLimit = 200;

        //CouponRuleEntity couponRuleEntity = getCouponRuleEntity();
        //Date expireTime = couponRuleEntity.getExpireTime();

        MulitCouponBoundleEntity mulitCouponBoundleEntity = new MulitCouponBoundleEntity();
        mulitCouponBoundleEntity.setOwnerId(clientId);
        mulitCouponBoundleEntity.setType(1);
        mulitCouponBoundleEntity.setGetMethod(method);
        mulitCouponBoundleEntity.setUseStatus(0);
        if(maxLimit == 0){
            mulitCouponBoundleEntity.setCouponValue(bd);
        }else{
            BigDecimal clientActivityCoinsVolume = getClientActivityCoinsVolume(clientId);
            BigDecimal sum = bd.add(clientActivityCoinsVolume);
            if(clientActivityCoinsVolume.doubleValue() < maxLimit.doubleValue()){

                if(sum.doubleValue() <= maxLimit.doubleValue()){
                    mulitCouponBoundleEntity.setCouponValue(bd);
                }else{
                    BigDecimal sub = new BigDecimal(maxLimit+"");
                    sub = sub.subtract(clientActivityCoinsVolume);
                    mulitCouponBoundleEntity.setCouponValue(sub);
                }
            }else{
                return;
            }
        }
        mulitCouponBoundleEntity.setConsumeValue(new BigDecimal("0"));
        mulitCouponBoundleEntity.setGotPmt(new Date());
        Date expireTime = TimeUtil.calculateAddDate(validity, actityValidityUnit);
        mulitCouponBoundleEntity.setExpirePmt(expireTime);

        mulitCouponBoundleDao.insert(mulitCouponBoundleEntity);
    }

    @Override
    public IPage<MulitCouponBoundleEntity> getRecordByClientId(Long clientUser_id,boolean onlyEnable,Integer page,Integer num){
        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);

        if(onlyEnable){
            queryWrapper.ge("expire_pmt",new Date());
        }

        Integer indexs = page == null?0:page;
        Integer pagesNums = num==null?10:num;
        if(indexs >0)
            indexs --;

        Page<MulitCouponBoundleEntity> map = new Page<MulitCouponBoundleEntity>(indexs,pagesNums);
        IPage<MulitCouponBoundleEntity> pages = mulitCouponBoundleDao.selectPage(map, queryWrapper);

        return pages;
    }



}
