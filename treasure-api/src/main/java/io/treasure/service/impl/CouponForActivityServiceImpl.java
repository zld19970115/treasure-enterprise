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
import java.util.Date;

@Service
public class CouponForActivityServiceImpl implements CouponForActivityService {

    @Autowired(required = false)
    private MulitCouponBoundleDao mulitCouponBoundleDao;
    @Autowired(required = false)
    private ClientUserDao clientUserDao;

    private Double coinsLimit = 50d;


    public BigDecimal getClientActivityCoinsVolume(Long clientUser_id){

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("use_status",0);
        queryWrapper.ge("got_pmt",new Date());
        queryWrapper.le("expire_pmt",new Date());
        queryWrapper.select("sum(coupon_value - consume_value) as coupon_value");
        MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleDao.selectOne(queryWrapper);
        if(mulitCouponBoundleEntity == null)
            return new BigDecimal("0");
        return mulitCouponBoundleEntity.getCouponValue();
    }

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


    public boolean coinsIsEnable(Long clientUser_id,BigDecimal coins){
        BigDecimal clientCanUseTotalCoinsVolume = getClientCanUseTotalCoinsVolume(clientUser_id);
        if(coins.compareTo(clientCanUseTotalCoinsVolume)>0){
            return false;
        }
        return true;
    }

    public BigDecimal updateCoinsConsumeRecord(Long clientUser_id,BigDecimal coins){

        BigDecimal clientCanUseTotalCoinsVolume = getClientCanUseTotalCoinsVolume(clientUser_id);
        if(coins.compareTo(clientCanUseTotalCoinsVolume)>0){
            System.out.println("并发问题：抵扣超限,将提前扣除["+ TimeUtil.simpleDateFormat.format(new Date())+":"+clientUser_id+","+clientCanUseTotalCoinsVolume+"-"+coins+"]");
        }
        BigDecimal clientCoinsVolume = getClientActivityCoinsVolume(clientUser_id);
        //if(clientCoinsVolume)
        return null;

    }


}
