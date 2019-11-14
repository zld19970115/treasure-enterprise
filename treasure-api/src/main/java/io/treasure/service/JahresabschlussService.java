package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.*;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface JahresabschlussService extends CrudService<GoodEntity, GoodDTO> {
    List<GoodCategoryEntity> selectCategory(Map<String, Object> params);

    List<SlaveOrderDTO>  selectBYgoodID(long id, String startTime1,String endTime1 );
    List<GoodDTO> selectByCategoeyid(long categoeyId);
    List<MerchantOrderDTO> selectBymerchantId(Map<String, Object> params);
    List<MerchantWithdrawDTO> selectBymerchantId2(Map<String, Object> params);
}
