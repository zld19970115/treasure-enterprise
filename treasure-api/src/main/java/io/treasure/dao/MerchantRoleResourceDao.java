package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantRoleResourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MerchantRoleResourceDao extends BaseDao<MerchantRoleResourceEntity> {

    Long countByRid(@Param("rid") Long rid);

}
