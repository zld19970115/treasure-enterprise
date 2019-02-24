package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.CategoryDTO;
import io.treasure.entity.CategoryEntity;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
public interface CategoryService extends CrudService<CategoryEntity, CategoryDTO> {
    //显示数据
    void on(Long id,int status);
    //隐藏数据
    void off(Long id,int status);
}