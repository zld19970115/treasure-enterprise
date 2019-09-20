package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.UserCouponDTO;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.UserCouponEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCouponService extends CrudService<UserCouponEntity, UserCouponDTO> {

    List selectByUserId(Long userId, long martId, double money);
    double  selectGift(Long userId);
    UserCouponEntity   selectByCouponId(long couponId,long userId);
    List  selectMyCouponByUserId(long userId);
    List<MerchantCouponEntity>  selectMartCoupon(Long userId, long martId);
    List<MerchantCouponEntity> selectBymartId(long martId);
}
