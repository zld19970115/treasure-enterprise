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
     List<CardInfoDTO>  selectByNoCode();
    Result openCard(List<Long> ids,Long userId);
   void updateCode(String s ,long id);

}
