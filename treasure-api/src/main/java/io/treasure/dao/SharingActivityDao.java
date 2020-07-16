package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.TakeoutOrdersEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;


@Mapper
public interface SharingActivityDao extends BaseDao<SharingActivityEntity>{
    List<SharingActivityEntity> getOneByMerchantIdAndStatus(long MerchantId , Date now);
}
