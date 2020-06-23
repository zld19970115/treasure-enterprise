package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.OpinionDTO;
import io.treasure.entity.OpinionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OpinionDao extends BaseDao<OpinionEntity> {
   void insertOpinion( Map<String, Object> params);

   List<OpinionDTO> pageList(Map<String, Object> params);
}
