package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.UserGiftDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.UserGiftEntity;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.Date;

public interface UserGiftService extends CrudService<UserGiftEntity, UserGiftDTO> {
    ClientUserEntity selectBynumber(String userNumber);
    UserGiftEntity  selectStatus(String number,Integer password);
    void updateUnumberAndStatus(String userNumber,long id);
    void  updateGift(BigDecimal money,long id);
    void insertGift(UserGiftDTO dto);
}
