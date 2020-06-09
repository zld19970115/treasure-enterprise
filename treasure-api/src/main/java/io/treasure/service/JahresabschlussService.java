package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.*;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;

import java.util.List;
import java.util.Map;

public interface JahresabschlussService extends CrudService<GoodEntity, GoodDTO> {
    List<GoodCategoryDTO> selectCategory(Map<String, Object> params);

    List<SlaveOrderDTO>  selectBYgoodID(long id, String startTime1, String endTime1);
    List<GoodDTO> selectByCategoeyid(long categoeyId);
    List<MerchantOrderDTO> selectBymerchantId(Map<String, Object> params);
    List<MerchantWithdrawDTO> selectBymerchantId2(Map<String, Object> params);
    PageData<GoodCategoryDTO> selectByParams(Map<String, Object> params);
}
