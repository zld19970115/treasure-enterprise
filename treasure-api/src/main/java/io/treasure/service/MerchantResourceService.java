package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.MerchantResourceDto;
import io.treasure.dto.MerchantResourceSaveDto;
import io.treasure.dto.MerchantResourceShowDto;
import io.treasure.entity.MerchantResourceEntity;

import java.util.List;
import java.util.Map;

public interface MerchantResourceService extends CrudService<MerchantResourceEntity, MerchantResourceDto> {

    List<MerchantResourceShowDto> menuList(Map<String, Object> params);

    Result menuSave(MerchantResourceSaveDto dto);

    Result menuUpdate(MerchantResourceSaveDto dto);

    Result menuDel(Long id);

}
