package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.BusinessManaherUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessManaherUserDao  extends BaseDao<BusinessManaherUserEntity> {
    BusinessManaherUserEntity selectByuserId(Long userId);
}
