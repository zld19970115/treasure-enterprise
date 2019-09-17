package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.entity.MerchantCouponEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Mapper
public interface MerchantCouponDao extends BaseDao<MerchantCouponEntity> {
    void updateStatusById(long id,int status);

    MerchantCouponEntity getAllById(Long id);

    List<MerchantCouponDTO> getMoneyOffByMerchantId(long merchantId,long userId);
}