package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
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

            updateActivityCoinsConsumeRecord(clientUser_id,balance,orderId);//

        }else{
            //正常扣除
            BigDecimal canUseActivityCoins = getClientActivityCoinsVolume(clientUser_id);
            BigDecimal coinsLimitDB = new BigDecimal(coinsLimit+"");
            if(canUseActivityCoins.compareTo(coinsLimitDB)>0){
                canUseActivityCoins = coinsLimitDB;         //宝币限额
            }

            if(canUseActivityCoins.compareTo(coins)>=0){
                updateActivityCoinsConsumeRecord(clientUser_id,coins,orderId); //只扣除活动宝币里的值//
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

        boolean b = canConsume(orderId);
        if(!b){
            System.out.println("宝币消费记录更新异常,当前订单号:"+orderId+"已经存在,请及时处理!");
            return false;
        }

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
                mulitCouponBoundleDao.updateStatusByIds(maxedOuts, null,orderId);
            }

            //减掉剩余的余额
            if (surplusId != null) {
                List<Long> idTmp = new ArrayList<>();
                idTmp.add(surplusId);
                BigDecimal couponValueLast = mulitCouponBoundleDao.selectById(surplusId).getCouponValue();
                if(surplusCoins.compareTo(couponValueLast)==0){
                    mulitCouponBoundleDao.updateStatusByIds(idTmp, null,orderId);
                }else{
                    mulitCouponBoundleDao.updateStatusByIds(idTmp, surplusCoins,orderId);
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
    public void resumeActivityCoinsRecord(Long clientUser_id,BigDecimal coins,String orderId){

        boolean b = canResume(orderId);
        if(!b){
            System.out.println("当前消费记录不存在：无法返还");
            return;
        }

        if(coins.doubleValue()==0)
            return;
        //        CouponRuleEntity couponRuleEntity = getCouponRuleEntity();1
//        Date expireTime = couponRuleEntity.getExpireTime();
//
//        MulitCouponBoundleEntity entity = new MulitCouponBoundleEntity();
//        entity.setOwnerId(clientUser_id);
//        entity.setCouponValue(coins);
//        entity.setType(1);
//        entity.setGetMethod(4);
//        entity.setUseStatus(0);
//        entity.setGotPmt(new Date());
//        entity.setExpirePmt(expireTime);
//        mulitCouponBoundleDao.insert(entity);

        BigDecimal zero = new BigDecimal("0");
        List<MulitCouponBoundleEntity> mulitCouponBoundleEntities = mulitCouponBoundleDao.selectRecord(clientUser_id);

        for (MulitCouponBoundleEntity mulitCouponBoundleEntity : mulitCouponBoundleEntities) {
            BigDecimal consumeValue = mulitCouponBoundleEntity.getConsumeValue();
            if (coins.compareTo(consumeValue)> -1){
                mulitCouponBoundleEntity.setConsumeValue(zero);
                coins= coins.subtract(consumeValue);
                mulitCouponBoundleDao.updateById(mulitCouponBoundleEntity);

            }else {
                BigDecimal subtract1 = consumeValue.subtract(coins);
                mulitCouponBoundleEntity.setConsumeValue(subtract1);
                mulitCouponBoundleDao.updateById(mulitCouponBoundleEntity);
                break;
            }
            if (coins.compareTo(zero) == 0){
                break;
            }
        }
        if (coins.compareTo(zero)==1){
            List<MulitCouponBoundleEntity> mulitCouponBoundleEntities1 = mulitCouponBoundleDao.selectByStatus(clientUser_id);
            for (MulitCouponBoundleEntity mulitCouponBoundleEntity : mulitCouponBoundleEntities1) {
                BigDecimal couponValue = mulitCouponBoundleEntity.getCouponValue();
                if (coins.compareTo(couponValue)> -1){
                    mulitCouponBoundleEntity.setUseStatus(0);
                    mulitCouponBoundleEntity.setConsumeValue(zero);
                    coins= coins.subtract(couponValue);
                    mulitCouponBoundleDao.updateById(mulitCouponBoundleEntity);
                }else {
                    BigDecimal subtract = couponValue.subtract(coins);
                    mulitCouponBoundleEntity.setUseStatus(0);
                    mulitCouponBoundleEntity.setConsumeValue(subtract);
                    mulitCouponBoundleDao.updateById(mulitCouponBoundleEntity);
                    break;
                }
                if (coins.compareTo(zero) == 0){
                    break;
                }
            }
        }

        clearProcessingFlag(orderId);

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
            resumeActivityCoinsRecord(clientUser_id,activityCoins,orderId);
        System.out.println("45646564646");
    }

//=====================================================================================================================

    @Override
    public SignedRewardSpecifyTimeEntity getParamsById(Long id){
        Long requireId = id==null?1:id;
        SignedRewardSpecifyTimeEntity signedParamsById = signedRewardSpecifyTimeService.getSignedParamsById(requireId);
        return signedParamsById;
    }

    @Override
    public PageData pageList(Map<String, Object> params) {
        PageHelper.startPage(Integer.parseInt(params.get("page")+""),Integer.parseInt(params.get("limit")+""));
        com.github.pagehelper.Page page = (com.github.pagehelper.Page) mulitCouponBoundleDao.pageList(params);
        return new PageData<>(page.getResult(),page.getTotal());
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
        if(times < timesLimit){
            boolean b = allowJoinInTimeRange(signedParamsById, clientUser_id);
            if(b){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public void insertClientActivityRecord(Long clientId,BigDecimal bd,Integer method,Integer validity, ESharingRewardGoods.ActityValidityUnit actityValidityUnit) throws ParseException {
        Integer maxLimit = 200;

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
        if(clientUser_id != null)
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

    /**
     *  规定时间内仅能抢一次,超限则显示false
     * @return
     */
    public boolean allowJoinInTimeRange(SignedRewardSpecifyTimeEntity entity,Long clientUser_id){
        Integer days = entity.getOnceInTimeRange();
        Date beforeTime = TimeUtil.getBeforeTime(days);

        String format = TimeUtil.simpleDateFormat.format(beforeTime);
        System.out.println(format);
        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);
        queryWrapper.ge("got_pmt",beforeTime);

        Integer res = mulitCouponBoundleDao.selectCount(queryWrapper);
        if(res==null)
            res = 0;
        if(res>0)
            return false;
        return true;
    }

    public Boolean checkClientDrawTimes(Long clientId) throws ParseException {
        Date date = new Date();
        String current = TimeUtil.sdfYmd.format(date)+ " 00:00:00";
        Date parse = TimeUtil.simpleDateFormat.parse(current);
        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientId);
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);
        queryWrapper.ge("got_pmt",parse);

        Integer res = mulitCouponBoundleDao.selectCount(queryWrapper);
        if(res==null)
            res = 0;
        if(res>0)
            return false;
        return true;
    }

    public BigDecimal getCanUseCurrentActivityRewardAmount(Long clientId){
        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        if(clientId == null) return new BigDecimal("1000");

        queryWrapper.eq("owner_id",clientId);
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);
        queryWrapper.ge("expire_pmt",new Date());
        queryWrapper.select("sum(coupon_value - consume_value) as coupon_value,id");

        List<MulitCouponBoundleEntity> mulitCouponBoundleEntities = mulitCouponBoundleDao.selectList(queryWrapper);
        if(mulitCouponBoundleEntities.size()>0){
            MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleEntities.get(0);
            if(mulitCouponBoundleEntity != null){
                BigDecimal couponValue = mulitCouponBoundleEntity.getCouponValue();
                return couponValue;
            }else{
                return  new BigDecimal("0");
            }

        }else{
            return new BigDecimal("0");
        }
    }

    @Override
    public BigDecimal getClientSharingActivityCoinsVolume(Long clientUser_id){

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("use_status",0);
        queryWrapper.eq("get_method",2);
        queryWrapper.ge("expire_pmt",now());
        queryWrapper.select("sum(coupon_value - consume_value) as coupon_value");
        MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleDao.selectOne(queryWrapper);
        if(mulitCouponBoundleEntity == null)
            return new BigDecimal("0");
        return mulitCouponBoundleEntity.getCouponValue();
    }


    /**
     * 检查是否允许恢复,恢复完成后，需要清除原来的这个对象的标记录
     * @return
     */
    public boolean canResume(String orderId){
        Integer integer = mulitCouponBoundleDao.selectCountByProccessNo(orderId);
        if(integer == null ||integer == 0)
            return false;
        return true;
    }

    /**
     * 清除对象的订单标记
     * @param orderId
     */
    public void clearProcessingFlag(String orderId){
        mulitCouponBoundleDao.clearProcessingFlag(orderId);
    }
    /**
     * 检查是否允许消费宝币，同一个单号在活动中仅能使用一次,如果记录中已经扣除过则不能进行再扣除
     * @return
     */
    public boolean canConsume(String orderId){
        Integer integer = mulitCouponBoundleDao.selectCountByProccessNo(orderId);
        if(integer == null || integer ==0)
            return true;
        return false;
    }

}
