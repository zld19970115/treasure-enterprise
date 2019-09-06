package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.GoodEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Mapper
public interface GoodDao extends BaseDao<GoodEntity> {
    //根据菜品名称和商户id
    List getByNameAndMerchantId(@Param("name") String name, @Param("martId") long martId);
    //上架、下架商品
    void updateStatusById(long id,int status);
}