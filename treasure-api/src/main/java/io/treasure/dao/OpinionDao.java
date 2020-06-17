package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.OpinionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface OpinionDao extends BaseDao<OpinionEntity> {
   void insertOpinion(Map<String, Object> params);
}
