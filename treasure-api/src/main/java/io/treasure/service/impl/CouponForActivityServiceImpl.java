package io.treasure.service.impl;

import io.treasure.dao.MulitCouponBoundleDao;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.service.CouponForActivityService;
import io.treasure.vo.ClientCoinsForActivityQueryVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CouponForActivityServiceImpl implements CouponForActivityService {

    @Autowired(required = false)
    private MulitCouponBoundleDao mulitCouponBoundleDao;


    public BigDecimal getClientCoinsVolume(ClientCoinsForActivityQueryVo vo){
        MulitCouponBoundleEntity entity = vo.getMulitCouponBoundleEntity();

        return null;
    }


}
