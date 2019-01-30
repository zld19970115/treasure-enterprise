package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.GoodEntity;
import org.apache.ibatis.annotations.Mapper;

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
    List getByNameAndMerchantId(String name ,long martId);
}