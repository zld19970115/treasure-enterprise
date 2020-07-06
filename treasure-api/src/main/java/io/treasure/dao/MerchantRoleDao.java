package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantRoleShowDto;
import io.treasure.dto.RoleUserDto;
import io.treasure.entity.MerchantRoleEntity;
import io.treasure.entity.MerchantRoleResourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MerchantRoleDao extends BaseDao<MerchantRoleEntity> {

    List<MerchantRoleShowDto> roleList(Map<String, Object> params);

    List<RoleUserDto> roleUserList(@Param("userId") Long userId);

}
