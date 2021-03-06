package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.entity.RecordGiftEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface RecordGiftDao extends BaseDao<RecordGiftEntity> {
   void insertRecordGift(@Param("userId") long userId, @Param("date") Date date, @Param("balanceGift") BigDecimal balanceGift, @Param("useGift") BigDecimal useGift);
   void insertRecordBalance(@Param("userId")long userId, @Param("date")Date date, @Param("balance")BigDecimal balance,@Param("useBalance") BigDecimal useBalance);
   void insertRecordGift2(@Param("userId") long userId, @Param("date") Date date, @Param("balanceGift") BigDecimal balanceGift, @Param("useGift") BigDecimal useGift);
   void insertRecordGift6(@Param("userId") long userId, @Param("date") Date date, @Param("balanceGift") BigDecimal balanceGift, @Param("useGift") BigDecimal useGift);
   List<RecordGiftDTO> selectByUserId(Map<String, Object> params);
   List<RecordGiftDTO> selectByUserIdandstatus(@Param("userId") long userId,@Param("status") int status);
  int insertRecordbyGiftAdmin( @Param("userId")long userId,@Param("transferredMobile")String transferredMobile,@Param("useGift")BigDecimal useGift,@Param("balanceGift")BigDecimal balanceGift,@Param("status")int status,@Param("createDate")Date createDate,@Param("creator")long creator);
   int insertRecordGiftAdmin(Map<String, Object> params);
   List<RecordGiftDTO> getAllRecordGoht(Map<String, Object> params);
   List<RecordGiftDTO> getRecordGiftByUserId(Map<String, Object> params);

}
