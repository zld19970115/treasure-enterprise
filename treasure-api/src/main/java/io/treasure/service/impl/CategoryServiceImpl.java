package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.CategoryDao;
import io.treasure.dto.CategoryDTO;
import io.treasure.entity.CategoryEntity;
import io.treasure.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Service
public class CategoryServiceImpl extends CrudServiceImpl<CategoryDao, CategoryEntity, CategoryDTO> implements CategoryService {
    /**
     * 显示数据
     * @param id
     */
    @Override
    public void on(Long id,int status) {
        baseDao.on(id,status);
    }

    /**
     * 隐藏数据
     * @param id
     */
    @Override
    public void off(Long id,int status) {
        baseDao.off(id,status);
    }

    /**
     * 根据分类名称和商户Id
     * @param name
     * @param merchantId
     * @return
     */
    @Override
    public List getByNameAndMerchantId(String name, long merchantId) {
        return baseDao.getByNameAndMerchantId(name,merchantId);
    }

    @Override
    public QueryWrapper<CategoryEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //菜品名称
        String name=(String)params.get("name");
        //商户Id
        String merchantId=(String)params.get("merchantId");
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);
        wrapper.like(StringUtils.isNotBlank(name),"name",name);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        return wrapper;
    }


}