package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.UserLevelUpdateEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserLevelUpdateDao extends BaseDao<UserLevelUpdateEntity> {

    UserLevelUpdateEntity getByClientId(Long clientId);

}
