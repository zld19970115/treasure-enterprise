package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.entity.GoodCategoryEntity;

import java.util.List;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
public interface GoodCategoryService extends CrudService<GoodCategoryEntity, GoodCategoryDTO> {
    //显示数据
    void on(Long id,int status);
    //隐藏数据
    void off(Long id,int status);
    //根据分类名称和商户Id查询数据
    List getByNameAndMerchantId(String name,long merchantId);
    //根据商户显示分类
    List getAllByMerchantId (long merchantId);
    //删除
    void remove(Long id,int status);
}