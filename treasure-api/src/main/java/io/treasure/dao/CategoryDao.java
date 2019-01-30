package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Mapper
public interface CategoryDao extends BaseDao<CategoryEntity> {
    //显示
    void on(Long id,int status);
    //隐藏
    void off(Long id,int status);
    //根据分类名称和商户Id
    List getByNameAndMerchantId(String name, long merchantId);
    //根据商户显示分类信息
    List getAllByMerchantId(long merchantId);
}