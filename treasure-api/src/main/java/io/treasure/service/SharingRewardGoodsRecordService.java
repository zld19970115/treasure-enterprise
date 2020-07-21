package io.treasure.service;

import io.treasure.entity.SharingRewardGoodsRecordEntity;
import io.treasure.vo.SharingRewardGoodsRecordComboVo;

import java.util.List;

public interface SharingRewardGoodsRecordService {

    //插入新商品券记录，即给用户增加记录
    void insertItem(SharingRewardGoodsRecordEntity expectantEntity);

    //更改商品券的状态
    void updateItemById(SharingRewardGoodsRecordEntity updateEntity);

    //取得商品券列表
    List<SharingRewardGoodsRecordEntity> getRecords(SharingRewardGoodsRecordComboVo conditionEntity);

}
