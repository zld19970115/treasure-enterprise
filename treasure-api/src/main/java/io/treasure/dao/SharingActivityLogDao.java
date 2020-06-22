package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingActivityLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface SharingActivityLogDao extends BaseDao<SharingActivityLogEntity> {

    Integer getSum(@Param("initiatorId") Long initiatorId,
                   @Param("saId") int saId,
                   @Param("proposeSequeueNo")int propose_sequeue_no);

    Integer getHelpCount(@Param("intitiatorId")long intitiatorId,
                         @Param("activityId")Integer activityId,
                         @Param("proposeSequeueNo")Integer proposeSequeueNo);
    Integer getHelpCountxx(String mobile);


}
