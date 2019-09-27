
package io.treasure.service.impl;

import io.treasure.common.constant.Constant;
import io.treasure.dao.CategoryDao;
import io.treasure.dto.CategoryDTO;
import io.treasure.entity.CategoryEntity;
import io.treasure.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
@Service
public class CategoryServiceImpl extends CrudServiceImpl<CategoryDao, CategoryEntity, CategoryDTO> implements CategoryService {

    @Override
    public QueryWrapper<CategoryEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        //状态
        String status = (String)params.get("status");
        //是否推荐
        String showInCommend = (String)params.get("showInCommend");
        //是否导航栏
        String showInNav = (String)params.get("showInCommend");
        //父级节点
        String pid=(String)params.get("pid");
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(showInCommend), "show_in_commend", showInCommend);
        wrapper.eq(StringUtils.isNotBlank(showInNav), "show_in_nav", showInNav);
        wrapper.eq(StringUtils.isNotBlank(pid),"pid", pid);
        return wrapper;
    }

    /**
     * 查询分类信息
     * @param id
     * @return
     */
    @Override
    public List<CategoryEntity> getListById(List<Long> id) {
        return baseDao.getListById(id);
    }
}