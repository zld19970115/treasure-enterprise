package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.CouponRuleDao;
import io.treasure.entity.CouponRuleEntity;
import io.treasure.service.CouponRuleForActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponRuleForActivityServiceImpl implements CouponRuleForActivityService {

    @Autowired(required = false)
    private CouponRuleDao couponRuleDao;

    @Override
    public CouponRuleEntity getByTypeAndId(Integer subjectType,Integer subjectId){
        QueryWrapper<CouponRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subject_type",subjectType);
        queryWrapper.eq("subject_id",subjectId);

        List<CouponRuleEntity> couponRuleEntities = couponRuleDao.selectList(queryWrapper);
        if(couponRuleEntities.size() != 0){
            return couponRuleEntities.get(0);
        }
        return null;
    }

}
