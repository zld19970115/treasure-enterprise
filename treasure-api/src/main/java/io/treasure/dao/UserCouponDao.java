package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.UserCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCouponDao  extends BaseDao<UserCouponEntity> {
    List selectByUserId(@Param("userId") Long userId, @Param("martId") long martId, @Param("money") double money);
    double  selectGift(Long userId);
    UserCouponEntity   selectByCouponId(@Param("couponId") long couponId,@Param("userId") long userId);
    List  selectMyCouponByUserId(long userId);
    List<MerchantCouponEntity>   selectMartCoupon(@Param("userId") Long userId, @Param("martId") long martId);
   List<MerchantCouponEntity> selectBymartId(long martId);
}
