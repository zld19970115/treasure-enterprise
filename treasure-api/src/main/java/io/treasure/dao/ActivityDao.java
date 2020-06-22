package io.treasure.dao;

import io.treasure.dto.ActivityRartakeDto;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.ActivityGiveEntity;
import io.treasure.entity.ActivityGiveLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityDao {

    List<ActivityEntity> activityPage(Map<String, Object> map);

    int del(@Param("id") Long id);

    int update(ActivityEntity dto);

    int insert(ActivityEntity dto);

    int updateGive(ActivityGiveEntity dto);

    int insertGive(ActivityGiveEntity dto);

    int insertGiveLog(ActivityGiveLogEntity dto);

    ActivityEntity selectById(@Param("id") Long id);

    ActivityGiveEntity selectGiveById(@Param("id") Long id);

    int receiveGift(@Param("activityId") Long activityId, @Param("userId") Long userId);

    int activityRartake(@Param("activityId") Long activityId, @Param("userId") Long userId);

    int cancellationUser(@Param("activityId") Long activityId, @Param("mobile") String mobile);
    int cancellationUser_bk(@Param("activityId") Long activityId, @Param("mobile") String mobile);

    ActivityEntity getHotActivity();

}
