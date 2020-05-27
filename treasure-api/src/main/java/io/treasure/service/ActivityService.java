package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.ActivityDto;
import io.treasure.dto.ActivityRartakeDto;
import io.treasure.dto.ReceiveGiftDto;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.ActivityGiveEntity;
import io.treasure.entity.ActivityGiveLogEntity;
import io.treasure.vo.ActivityRartakeVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface ActivityService {

    PageData<ActivityEntity> activityPage(Map<String, Object> map);

    int del(Long id);

    int update(ActivityDto dto);

    int insert(ActivityDto dto);

    int updateGive(ActivityGiveEntity dto);

    int insertGive(ActivityGiveEntity dto);

    int insertGiveLog(ActivityGiveLogEntity dto);

    int receiveGift(ReceiveGiftDto dto);

    ActivityDto selectById(Long id);

    ActivityGiveEntity selectGiveByActivityId(Long activityId);

    Result<ActivityRartakeVo> activityRartake(ActivityRartakeDto dto);

}
