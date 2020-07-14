package io.treasure.dao;

import io.treasure.entity.ClientLevelRuleEntity;

public interface ClientLevelRuleDao {
    int insert(ClientLevelRuleEntity record);

    int insertSelective(ClientLevelRuleEntity record);
}