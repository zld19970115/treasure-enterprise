package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingActivityExtendsEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface SharingActivityExtendsDao extends BaseDao<SharingActivityExtendsEntity>{

    void insertOne(SharingActivityExtendsEntity saExtendsEntity);
}
