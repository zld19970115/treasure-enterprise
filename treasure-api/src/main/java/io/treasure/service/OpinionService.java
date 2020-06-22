package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.AppVersionDTO;
import io.treasure.dto.OpinionDTO;
import io.treasure.entity.OpinionEntity;

import java.util.Map;

public interface OpinionService extends CrudService<OpinionEntity, OpinionDTO> {
    void insertOpinion( Map<String, Object> params);

    PageData<OpinionDTO> pageList(Map<String, Object> params);
}
