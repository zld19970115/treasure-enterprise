package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.entity.RecordGiftEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface RecordGiftService  extends CrudService<RecordGiftEntity, RecordGiftDTO> {

    void insertRecordGift(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift);
    void insertRecordGift2(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift);

    PageData<RecordGiftDTO> selectByUserId(Map<String, Object> params);
}
