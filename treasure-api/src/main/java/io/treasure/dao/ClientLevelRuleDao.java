package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.ClientLevelRuleEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ClientLevelRuleDao extends BaseDao<ClientLevelRuleEntity> {

   // ClientLevelRuleEntity selectLevelRule();
}