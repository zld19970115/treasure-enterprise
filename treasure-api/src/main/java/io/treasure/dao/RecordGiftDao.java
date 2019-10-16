package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.RecordGiftEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface RecordGiftDao extends BaseDao<RecordGiftEntity> {
   void insertRecordGift(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift);
   void insertRecordGift2(long userId, Date date, BigDecimal balanceGift, BigDecimal useGift);
}
