package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.RecordGiftDao;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.entity.RecordGiftEntity;
import io.treasure.service.RecordGiftService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class RecordGiftServiceImpl  extends CrudServiceImpl<RecordGiftDao, RecordGiftEntity, RecordGiftDTO> implements RecordGiftService {
    @Override
    public QueryWrapper<RecordGiftEntity> getWrapper(Map<String, Object> params) {
        return null;
    }

    @Override
    public void insertRecordGift(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift) {
      baseDao.insertRecordGift(userId,date,balanceGift,useGift);
    }

    @Override
    public void insertRecordGift2(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift) {
        baseDao.insertRecordGift2(userId,date,balanceGift,useGift);
    }
}
