package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.UserGiftEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface UserGiftDao  extends BaseDao<UserGiftEntity> {
     ClientUserEntity selectBynumber(String userNumber);
    UserGiftEntity selectStatus(@Param("number") Long number,@Param("password") Integer password);
    void updateUnumberAndStatus(@Param("userNumber")String userNumber,@Param("id")long id);
    void  updateGift(@Param("money")BigDecimal money,@Param("id")long id);
}
