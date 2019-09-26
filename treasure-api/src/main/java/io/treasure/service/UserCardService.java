package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.CardInfoDTO;
import io.treasure.entity.CardInfoEntity;

public interface UserCardService extends CrudService<CardInfoEntity, CardInfoDTO> {
    Result selectByIdAndPassword(long id , String password,long userId);
}
