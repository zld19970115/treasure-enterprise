package io.treasure.service;

import com.alipay.api.AlipayApiException;
import io.treasure.common.utils.Result;
import io.treasure.entity.MerchantSalesRewardRecordEntity;

public interface CommissionWithdrawService {

    Result wxMerchantCommissionWithDraw(MerchantSalesRewardRecordEntity entity);
    Result AliMerchantCommissionWithDraw(MerchantSalesRewardRecordEntity entity) throws AlipayApiException;
}
