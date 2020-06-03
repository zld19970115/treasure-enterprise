package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.TakeoutOrdersDetailEntity;
import io.treasure.entity.TakeoutOrdersEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 外卖订单DAO
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@Mapper
public interface TakeoutOrdersDetailDao extends BaseDao<TakeoutOrdersDetailEntity> {


}