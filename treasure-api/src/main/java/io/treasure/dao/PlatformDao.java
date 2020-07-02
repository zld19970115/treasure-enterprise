package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.PlatformDto;
import io.treasure.entity.PlatformEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PlatformDao extends BaseDao<PlatformEntity> {

    List<PlatformDto> pageList(Map<String, Object> params);

}
