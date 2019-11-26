package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.OpinionDTO;
import io.treasure.entity.OpinionEntity;

import java.util.Map;

public interface OpinionService extends CrudService<OpinionEntity, OpinionDTO> {
    void insertOpinion( Map<String, Object> params);
}
