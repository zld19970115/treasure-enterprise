package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.DistributionRelationshipEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DistributionRewardDao extends BaseDao<DistributionRelationshipEntity> {


    DistributionRelationshipEntity selectByslaverUser(String mobileSlaver);
    DistributionRelationshipEntity selectByMasterUser(String mobileMaster);
    int  selectRadio(int saId);
}
