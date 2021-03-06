package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.dao.GoodCategoryDao;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.service.GoodCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Override
    public List<GoodCategoryDTO> getAllByMerchantIds(Map<String,Object> params) {
        return baseDao.getAllByMerchantIds(params);
    }

    @Override
    public List<GoodCategoryDTO> getAllByMerchantIdsByOutside(Map<String, Object> params) {
        return baseDao.getAllByMerchantIdsByOutside(params);
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
        IPage<GoodCategoryEntity> pages=getPage(params,(String) params.get("ORDER_FIELD"),false);
        System.out.println(params.get("ORDER_FIELD")+"4564665446546654465");
        List<GoodCategoryDTO> list=baseDao.listPage(params);
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
        //是否推荐
        String showInCommend=(String)params.get("showInCommend");
        List<Long> mId=new ArrayList<Long>();
        if(StringUtils.isNotBlank(merchantId)){
            String[] mIds=merchantId.split(",");
            for(int i=0;i<mIds.length;i++){
                mId.add(Long.parseLong(mIds[i]));
            }
        }
        QueryWrapper<GoodCategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);
        wrapper.like(StringUtils.isNotBlank(name),"name",name);
        wrapper.in(StringUtils.isNotBlank(merchantId),"merchant_id",mId);
        wrapper.eq(StringUtils.isNotBlank(showInCommend),"show_in_commend",showInCommend);
        return wrapper;
    }


}