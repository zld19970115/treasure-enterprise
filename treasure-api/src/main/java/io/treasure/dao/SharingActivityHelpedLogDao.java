package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.SharingActivityHelpedEntity;
import io.treasure.entity.SharingActivityLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface SharingActivityHelpedLogDao extends BaseDao<SharingActivityHelpedEntity> {


    List<SharingActivityHelpedEntity> selectHelpedListCombo(@Param("initiatorId") Long initiatorId,
                                                            @Param("activityId") int activityId);

    List<SharingActivityHelpedEntity> selectHelpedListComboUnread(@Param("initiatorId") Long initiatorId,
                                                            @Param("activityId") int activityId);
    
}
