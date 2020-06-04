package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.TakeoutOrdersEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 外卖订单DAO
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Mapper
public interface TakeoutOrdersDao extends BaseDao<TakeoutOrdersEntity> {


}