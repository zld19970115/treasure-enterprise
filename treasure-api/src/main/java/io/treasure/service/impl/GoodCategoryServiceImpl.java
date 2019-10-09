package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.GoodCategoryDao;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.service.GoodCategoryService;
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
public class GoodCategoryServiceImpl extends CrudServiceImpl<GoodCategoryDao, GoodCategoryEntity, GoodCategoryDTO> implements GoodCategoryService {
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
    public List getAllByMerchantId(long merchantId) {
        return baseDao.getAllByMerchantId(merchantId);
    }

    /**
     * 删除
     * @param id
     * @param status
     */
    @Override
    public void remove(Long id, int status) {
        baseDao.on(id,status);
    }

    /**
     * 列表显示同时显示店铺名称
     * @param params
     * @return
     */
    @Override
    public PageData<GoodCategoryDTO> selectPage(Map<String, Object> params) {
        IPage<GoodCategoryEntity> pages=getPage(params, Constant.CREATE_DATE,false);
        List<GoodCategoryDTO> list=baseDao.selectPage(params);
        return getPageData(list,pages.getTotal(), GoodCategoryDTO.class);
    }

    @Override
    public QueryWrapper<GoodCategoryEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //菜品名称
        String name=(String)params.get("name");
        //商户Id
        String merchantId=(String)params.get("merchantId");
        QueryWrapper<GoodCategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);
        wrapper.like(StringUtils.isNotBlank(name),"name",name);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        return wrapper;
    }


}