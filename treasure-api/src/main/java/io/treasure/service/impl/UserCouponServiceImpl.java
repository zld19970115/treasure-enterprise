package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.UserCouponDao;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.dto.UserCouponDTO;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.UserCouponEntity;
import io.treasure.service.UserCouponService;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Service
public class UserCouponServiceImpl  extends CrudServiceImpl<UserCouponDao, UserCouponEntity, UserCouponDTO> implements UserCouponService {
    @Override
    public QueryWrapper<UserCouponEntity> getWrapper(Map<String, Object> params) {
        String id = (String)params.get("id");

        //状态
        String status = (String)params.get("status");
        String merchantId=(String)params.get("martId");
        //商户id
        QueryWrapper<UserCouponEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"mart_id",merchantId);
        return wrapper;
    }


    @Override
    public List<MerchantCouponDTO> selectByUserId(Long userId, long martId, double money) {
        return baseDao.selectByUserId(userId,martId,money);
    }

    @Override
    public BigDecimal selectGift(Long userId) {
        return baseDao.selectGift(userId);
    }

    @Override
    public UserCouponEntity selectByCouponId(long couponId,long userId) {
        return baseDao.selectByCouponId(couponId,userId);
    }

    @Override
    public List<MerchantCouponDTO> selectMyCouponByUserId(long userId) {
        return baseDao.selectMyCouponByUserId(userId);
    }

    @Override
    public  List<MerchantCouponEntity>  selectMartCoupon(Long userId, long martId) {
        return baseDao.selectMartCoupon(userId,martId);
    }

    @Override
    public List<MerchantCouponEntity> selectBymartId(long martId) {
        return baseDao.selectBymartId(martId);
    }

    @Override
    public void updateStatus(long couponId) {
        baseDao.updateStatus(couponId);
    }


}
