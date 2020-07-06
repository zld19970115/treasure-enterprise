package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantRoleResourceEntity;
import io.treasure.entity.MerchantUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MerchantUserRoleDao extends BaseDao<MerchantUserRoleEntity> {

    Long countByRole(@Param("roleId") Long roleId);

    int userRoleDel(Map<String, Object> params);

    int userRoleSave(Map<String, Object> params);

    MerchantUserRoleEntity selectByUserId(Long userId);

}
