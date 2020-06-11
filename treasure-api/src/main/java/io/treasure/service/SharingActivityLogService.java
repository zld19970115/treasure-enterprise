package io.treasure.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.entity.SharingActivityLogEntity;

import java.util.List;

public interface SharingActivityLogService {

    List<SharingActivityLogEntity> getList(Long intitiatorId, Integer activityId);

    /**
     * 取得指定用户参加的次数
     * @param intitiatorId
     * @param activityId
     * @return
     */
    Integer getCount(Long intitiatorId, Integer activityId);

    /**
     * 检查用户助力的次数，非新用户才有效时可用
     * @param intitiatorId
     * @param activityId
     * @param helperMobile
     * @return
     */
    Integer getHelpedCount(Long intitiatorId, Integer activityId, String helperMobile);


    /**
     * 插入新记录，向助力日志中
     * @param
     * @return
     */
    boolean insertOne(SharingActivityLogEntity sharingActivityLogEntity);

    Integer getSum(Long intitiatorId, Integer activityId);
}
