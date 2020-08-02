package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.CategoryDTO;
import io.treasure.dto.CategoryPageDto;
import io.treasure.entity.CategoryEntity;

import java.util.List;
import java.util.Map;


/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
public interface CategoryService extends CrudService<CategoryEntity, CategoryDTO> {
    //根据id查询
    List<CategoryEntity> getListById(List<Long> id);

    PageData<CategoryPageDto> pageList(Map<String, Object> params);

    List<CategoryEntity> getListByIds(List<Long> id);


}