package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.CardInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface UserCardDao  extends BaseDao<CardInfoEntity> {
    CardInfoEntity  selectByIdAndPassword(@Param("id") long id , @Param("password")String password);

}
