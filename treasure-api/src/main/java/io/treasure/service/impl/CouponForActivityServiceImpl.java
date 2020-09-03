package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.CouponRuleDao;
import io.treasure.dao.MulitCouponBoundleDao;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.service.CouponForActivityService;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.ClientCoinsForActivityQueryVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CouponForActivityServiceImpl implements CouponForActivityService {

    @Autowired(required = false)
    private MulitCouponBoundleDao mulitCouponBoundleDao;
    @Autowired(required = false)
    private ClientUserDao clientUserDao;

    private Double coinsLimit = 50d;

    @Override
    public BigDecimal getClientActivityCoinsVolume(Long clientUser_id){

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("use_status",0);
        queryWrapper.ge("got_pmt",new Date());
        queryWrapper.le("expire_pmt",new Date());
        //queryWrapper.groupBy("owner_id");
        queryWrapper.select("sum(coupon_value - consume_value) as coupon_value");
        MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleDao.selectOne(queryWrapper);
        if(mulitCouponBoundleEntity == null)
            return new BigDecimal("0");
        return mulitCouponBoundleEntity.getCouponValue();
    }
    @Override
    public BigDecimal getClientCanUseTotalCoinsVolume(Long clientUser_id){
        BigDecimal clientActivityCoinsVolume = getClientActivityCoinsVolume(clientUser_id);
        if(clientActivityCoinsVolume.doubleValue()>coinsLimit){
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
    @Override
    public void updateCoinsConsumeRecord(Long clientUser_id,BigDecimal coins){

        BigDecimal clientCanUseTotalCoinsVolume = getClientCanUseTotalCoinsVolume(clientUser_id);
        if(coins.compareTo(clientCanUseTotalCoinsVolume)>0){
            System.out.println("并发问题：抵扣超限,将提前扣除["+ TimeUtil.simpleDateFormat.format(new Date())+":"+clientUser_id+","+clientCanUseTotalCoinsVolume+"-"+coins+"]");

            BigDecimal clientCoinsVolume = getClientActivityCoinsVolume(clientUser_id);

            ClientUserEntity clientUserEntity = clientUserDao.selectById(clientUser_id);
            BigDecimal balance = clientUserEntity.getBalance();
            clientUserEntity.setBalance(new BigDecimal("0"));
            clientUserDao.updateById(clientUserEntity);
            BigDecimal subtract = coins.subtract(balance);

            updateActivityCoinsConsumeRecord(clientUser_id,balance);

        }else{
            //正常扣除
            BigDecimal canUseCoins = getClientCanUseTotalCoinsVolume(clientUser_id);
            if(canUseCoins.compareTo(coins)>=0){
                updateActivityCoinsConsumeRecord(clientUser_id,coins);
            }else{
                updateActivityCoinsConsumeRecord(clientUser_id,canUseCoins);
                BigDecimal subtract = coins.subtract(canUseCoins);

                ClientUserEntity clientUserEntity = clientUserDao.selectById(clientUser_id);
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
    public boolean updateActivityCoinsConsumeRecord(Long clientUser_id,BigDecimal coins){

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("use_status",0);
        queryWrapper.ge("got_pmt",new Date());
        queryWrapper.le("expire_pmt",new Date());
        queryWrapper.orderByAsc("expire_pmt");
        List<MulitCouponBoundleEntity> resourceEntities = null;
        try {
            resourceEntities = mulitCouponBoundleDao.selectList(queryWrapper);
            if(resourceEntities.size() == 0)
                return false;
        }catch(Exception e){
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
                mulitCouponBoundleDao.updateStatusByIds(idTmp, surplusCoins);
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

}
