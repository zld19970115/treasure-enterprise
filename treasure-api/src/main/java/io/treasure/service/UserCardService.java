package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.CardInfoDTO;
import io.treasure.entity.CardInfoEntity;

import java.util.List;
import java.util.Map;

public interface UserCardService extends CrudService<CardInfoEntity, CardInfoDTO> {
    Result selectByIdAndPassword(long id , String password,long userId);

    PageData<CardInfoDTO> pageList(Map map);

    Result openCard(List<String> ids);

}
