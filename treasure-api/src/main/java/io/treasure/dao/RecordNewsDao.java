package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.RecordGiftEntity;
import io.treasure.entity.RecordNewsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecordNewsDao extends BaseDao<RecordNewsEntity> {
    RecordNewsEntity selectByUandNid(@Param("id") Long id,@Param("nId") Long nId, @Param("type")int type);
    List<RecordNewsEntity> selectByUid(@Param("id")Long id, @Param("type")int type);
}
