package io.treasure.vo;

import io.treasure.entity.MerchantSalesRewardRecordEntity;
import lombok.Data;

import java.util.List;

@Data
public class SalesRewardApplyForWithdrawVo {

    private List<MerchantSalesRewardRecordEntity> entities;
    private Integer withDrawType;
}
