package io.treasure.vo;

import io.treasure.entity.SharingRewardGoodsRecordEntity;
import lombok.Data;

@Data
public class SharingRewardGoodsRecordComboVo {
    private SharingRewardGoodsRecordEntity sharingRewardGoodsRecordEntity;

    private Integer expireFlag;//1仅有效，2仅失效
}
