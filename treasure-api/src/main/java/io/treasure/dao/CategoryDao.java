package io.treasure.dao;
import io.treasure.entity.CategoryEntity;

import io.treasure.common.dao.BaseDao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Mapper
public interface CategoryDao extends BaseDao<CategoryEntity> {
    List<CategoryEntity> getListById(List<Long> id);
}