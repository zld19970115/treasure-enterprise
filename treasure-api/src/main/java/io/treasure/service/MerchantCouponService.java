package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.entity.MerchantCouponEntity;

/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface MerchantCouponService extends CrudService<MerchantCouponEntity, MerchantCouponDTO> {
    void updateStatusById(long id,int status);

    MerchantCouponEntity getAllById(Long id);
}