package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.StatsDayDetailEntity;
import io.treasure.service.StatsDayDetailService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台日交易明细表
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-24
 */
@Mapper
public interface StatsDayDetailDao  extends BaseDao<StatsDayDetailEntity> {

    int orderCount(@Param("orderId") String orderId);

}
