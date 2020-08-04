package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.DistributionParamsEntity;
import io.treasure.entity.DistributionRelationshipEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DistributionParamsDao extends BaseDao<DistributionParamsEntity> {
}
