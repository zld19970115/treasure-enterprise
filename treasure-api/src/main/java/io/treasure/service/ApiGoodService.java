package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.GoodDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.GoodEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
public interface ApiGoodService extends CrudService<GoodEntity, GoodDTO> {
    //根据商户id和菜品名称查询
    List getByNameAndMerchantId(String name, long martId);
    //上架商品
    void on(long id, int status);
    //下架商品
    void off(long id, int status);
    //删除
    void remove(long id, int status);
    //查询所有菜品分类id并去重
    List<GoodCategoryEntity> getGoodCategoryByMartId(long martId);

    //通过商户ID查询此商户所有菜品
    List getGoodsByMartId( Map<String, Object> params);

    //通过商户ID与菜品分类ID查询此分类的所有菜
    List getGoodsByGoodCategoryId(long martId, long goodCategoryId);

    //根据商户ID查询热销菜品
    List<GoodDTO> getShowInHotbyMartId(long martId);
}