package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.PlatformDto;
import io.treasure.entity.PlatformEntity;

import java.util.Map;

public interface PlatformService extends CrudService<PlatformEntity, PlatformDto> {

    PageData<PlatformDto> pageList(Map<String, Object> params);

    Result savePC(PlatformDto dto);

    Result updatePC(PlatformDto dto);

    Result delPC(Long id);

}
