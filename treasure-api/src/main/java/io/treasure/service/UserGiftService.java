package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.UserGiftDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.UserGiftEntity;

import java.math.BigDecimal;

public interface UserGiftService extends CrudService<UserGiftEntity, UserGiftDTO> {
    ClientUserEntity selectBynumber(String userNumber);
    UserGiftEntity  selectStatus(Long number,Integer password);
    void updateUnumberAndStatus(String userNumber,long id);
    void  updateGift(BigDecimal money,long id);
}
