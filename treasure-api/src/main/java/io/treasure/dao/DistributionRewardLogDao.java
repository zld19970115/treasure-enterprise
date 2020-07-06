package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.DistributionRelationshipEntity;
import io.treasure.entity.DistributionRewardLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DistributionRewardLogDao extends BaseDao<DistributionRewardLogEntity> {
}
