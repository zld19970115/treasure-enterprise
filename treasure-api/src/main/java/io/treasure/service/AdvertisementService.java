package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.AdvertisementDto;
import io.treasure.entity.AdvertisementEntity;

import java.util.Map;

public interface AdvertisementService extends CrudService<AdvertisementEntity, AdvertisementDto> {

    PageData<AdvertisementEntity> pageList(Map<String, Object> map);

}
