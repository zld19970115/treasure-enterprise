package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MasterOrderSimpleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MasterOrderSimpleDao extends BaseDao<MasterOrderSimpleEntity> {

    List<MasterOrderSimpleEntity> queryByOrder(@Param("merchantId") Long merchantId,@Param("status")Integer status);

}
