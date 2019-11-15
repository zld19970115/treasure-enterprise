package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.dto.MerchantWithdrawDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface JahresabschlussDao extends BaseDao<GoodEntity> {

  List<GoodCategoryEntity>  selectCategory(Map<String, Object> params);
  List<SlaveOrderDTO>  selectBYgoodID(@Param("id") long id, @Param("startTime1")  String startTime1,@Param("endTime1") String endTime1 );
    List<GoodDTO> selectByCategoeyid(long categoeyId);
  List<MerchantOrderDTO>  selectBymerchantId(Map<String, Object> params);
  List<MerchantWithdrawDTO> selectBymerchantId2(Map<String, Object> params);
}
