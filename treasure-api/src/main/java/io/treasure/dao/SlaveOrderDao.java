package io.treasure.dao;
import io.treasure.entity.SlaveOrderEntity;

import io.treasure.common.dao.BaseDao;

import org.apache.ibatis.annotations.Mapper;

/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Mapper
public interface SlaveOrderDao extends BaseDao<SlaveOrderEntity> {
	
}