package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.MerchantCouponDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.entity.MerchantCouponEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商户端优惠卷
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface MerchantCouponService extends CrudService<MerchantCouponEntity, MerchantCouponDTO> {
    void updateStatusById(long id, int status);

    MerchantCouponEntity getAllById(Long id);
    List<MerchantCouponDTO> getMoneyOffByMerchantId(long merchantId, long userId);
    Result getDifference(long merchantId, BigDecimal totalMoney, long userId);
    PageData<MerchantCouponDTO> listPage(Map<String, Object> param);
    BigDecimal getDiscountCount(Double orderTotalPrice, int merchantCouponId);
}