package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.dto.UserCouponDTO;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.UserCouponEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UserCouponService extends CrudService<UserCouponEntity, UserCouponDTO> {

    List<MerchantCouponDTO> selectByUserId(Long userId, long martId, double money);
    BigDecimal selectGift(Long userId);
    UserCouponEntity   selectByCouponId(long couponId,long userId);
    List<MerchantCouponDTO>  selectMyCouponByUserId(long userId);
    List<MerchantCouponEntity>  selectMartCoupon(Long userId, long martId);
    List<MerchantCouponEntity> selectBymartId(long martId);
     void   updateStatus(long couponId);
}
