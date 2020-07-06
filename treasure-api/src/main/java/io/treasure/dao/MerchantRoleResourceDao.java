package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantRoleResourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantRoleResourceDao extends BaseDao<MerchantRoleResourceEntity> {

    Long countByRid(@Param("rid") Long rid);

    Long countByRole(@Param("roleId") Long roleId);

    int delByRole(@Param("roleId") Long roleId);

    int saveRoleMenu(@Param("ids") List<Long> ids, @Param("roleId") Long roleId);

}
