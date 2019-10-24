package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.RecordGiftEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface RecordGiftDao extends BaseDao<RecordGiftEntity> {
   void insertRecordGift(@Param("userId") long userId,@Param("date") Date date,@Param("balanceGift") BigDecimal balanceGift,@Param("useGift") BigDecimal useGift);
   void insertRecordGift2(@Param("userId") long userId,@Param("date") Date date,@Param("balanceGift") BigDecimal balanceGift,@Param("useGift") BigDecimal useGift);
}
