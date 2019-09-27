package io.treasure.service;

import io.treasure.dto.CategoryDTO;
import io.treasure.entity.CategoryEntity;

import io.treasure.common.service.CrudService;

import java.util.List;


/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
public interface CategoryService extends CrudService<CategoryEntity, CategoryDTO> {
    //根据id查询
    List<CategoryEntity> getListById(List<Long> id);
}