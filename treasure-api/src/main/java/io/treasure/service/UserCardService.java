package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.CardInfoDTO;
import io.treasure.entity.CardInfoEntity;

public interface UserCardService extends CrudService<CardInfoEntity, CardInfoDTO> {
    CardInfoEntity  selectByIdAndPassword(long id , String password);
}
