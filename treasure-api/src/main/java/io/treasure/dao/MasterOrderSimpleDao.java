package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.OrderSimpleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MasterOrderSimpleDao extends BaseDao<OrderSimpleEntity> {

    List<OrderSimpleEntity> queryByOrder(@Param("merchantId") Long merchantId,
                                         @Param("index")Integer index,
                                         @Param("pages")Integer pages);

    Integer qureyCountByMerchantId(@Param("merchantId") Long merchantId);

}
