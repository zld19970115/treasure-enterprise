package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantResourceSaveDto;
import io.treasure.dto.MerchantResourceShowDto;
import io.treasure.entity.MerchantResourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MerchantResourceDao extends BaseDao<MerchantResourceEntity> {

    List<MerchantResourceShowDto> menuList(Map<String, Object> params);

    Long count(@Param("pid") Long pid);

    Long countMenuId(@Param("menuId") Long id);

    int add(MerchantResourceSaveDto dto);

    int updateMenu(MerchantResourceSaveDto dto);

    int del(@Param("id") Long id);

}
