package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.GoodPagePCDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;
import io.treasure.entity.MerchantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@Mapper
public interface ApiGoodDao extends BaseDao<GoodEntity> {
    //根据菜品名称和商户id
    List getByNameAndMerchantId(String name, long martId);

    //上架、下架商品
    void updateStatusById(long id, int status);

    //查询所有菜品分类id并去重
    List<GoodCategoryEntity> getGoodCategoryByMartId(long martId);

    //通过商户ID查询此商户所有菜品
    List<GoodEntity> getGoodsByMartId(Map<String, Object> params);
    List<GoodEntity> getoutsideGoodsByMartId(Map<String, Object> params);
    //通过商户ID与菜品分类ID查询此分类的所有菜
    List<GoodEntity> getGoodsByGoodCategoryId(@Param("martId") long martId, @Param("goodCategoryId") long goodCategoryId);
    List<GoodEntity>  getoutsideGoodsByGoodCategoryId(@Param("martId") long martId, @Param("goodCategoryId") long goodCategoryId);
    List<GoodDTO> getShowInHotbyMartId(long martId);

    List<GoodPagePCDTO> goodPageListPC(Map<String, Object> params);
}