package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.AdvertisementEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdvertisementDao extends BaseDao<AdvertisementEntity> {

    List<AdvertisementEntity> pageList(Map<String, Object> map);

}
