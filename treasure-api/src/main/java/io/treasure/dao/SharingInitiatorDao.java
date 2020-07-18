package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.SharingInitiatorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SharingInitiatorDao extends BaseDao<SharingInitiatorEntity> {

    int getCount(@Param("intitiatorId")Long intitiatorId,
                 @Param("saId")Integer saId,
                 @Param("status")Integer status);

    void closeActivity(@Param("intitiatorId")Long intitiatorId,@Param("saId")Integer saId);

    void setReadedStatus(@Param("intitiatorId")Long intitiatorId,
                         @Param("saId")Integer saId);

    void updateSuccessStatus(@Param("proposeId") Integer proposeId,@Param("helpedNum") Integer helpedNum);

    SharingInitiatorEntity getLastOne(@Param("intitiatorId")Long intitiatorId,@Param("saId")Integer saId);


}
