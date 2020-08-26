package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.RecordGiftDTO;
import io.treasure.dto.RecordNewsDTO;
import io.treasure.entity.RecordGiftEntity;
import io.treasure.entity.RecordNewsEntity;

import java.util.List;

public interface RecordNewsService extends CrudService<RecordNewsEntity, RecordNewsDTO> {
    RecordNewsEntity  selectByUandNid(Long id,Long nId,int type);
    List<RecordNewsEntity> selectByUid(Long id, int type);
}
