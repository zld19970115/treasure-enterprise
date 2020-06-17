package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.entity.MerchantCouponEntity;
import io.treasure.entity.UserCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserCouponDao  extends BaseDao<UserCouponEntity> {
    List<MerchantCouponDTO> selectByUserId(@Param("userId") Long userId, @Param("martId") long martId, @Param("money") double money);
    BigDecimal selectGift(Long userId);
    UserCouponEntity   selectByCouponId(@Param("couponId") long couponId, @Param("userId") long userId);
    List<MerchantCouponDTO>  selectMyCouponByUserId(long userId);
    List<MerchantCouponEntity>   selectMartCoupon(@Param("userId") Long userId, @Param("martId") long martId);
   List<MerchantCouponEntity> selectBymartId(long martId);
    void   updateStatus(long couponId);
}
