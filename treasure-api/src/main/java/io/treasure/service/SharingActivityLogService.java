package io.treasure.service;

import io.treasure.entity.SharingActivityHelpedEntity;
import io.treasure.entity.SharingActivityLogEntity;
import io.treasure.entity.SharingInitiatorEntity;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

public interface SharingActivityLogService {


    /**
     * 取得本活动参加助力的详情，指定用户
     * @param intitiatorId
     * @param activityId
     * @return
     */
    List<SharingActivityLogEntity> getList(long intitiatorId, Integer activityId,Integer proposeSequeueNo);

    /**
     * 取得指定用户参加的次数,及助力的次数
     * @param intitiatorId
     * @param activityId
     * @return
     */
    Integer getCount(long intitiatorId, Integer activityId,Integer proposeSequeueNo);

    /**
     * 插入新记录，向助力日志中
     * @param
     * @return
     */
    boolean insertOne(@NotEmpty SharingActivityLogEntity sharingActivityLogEntity);

    Integer getRewardSum(long intitiatorId, int activityId,int proposeSequeueNo);
    int getHelpedCount(String mobile);
    int getHelpedCount(String mobile, Date start, Date stop);
    SharingActivityLogEntity getOne(Long initiatorId,Integer activityId,Integer proposeSequeueNo,String helperMobile);

    List<SharingActivityLogEntity> getHelpedList(long intitiatorId, Integer activityId,Integer proposeSequeueNo);

    List<SharingActivityHelpedEntity> getHelpedListCombo(Long intitiatorId, Integer activityId);
    int getHelpedCountForCurrentSharing(long intitiatorId,int activityId,int proposeSequeueNo,String mobile);

    List<SharingInitiatorEntity> getUnreadedLogList(long intitiatorId, int activityId);
    List<SharingActivityHelpedEntity> getHelpedListComboUnread(Long intitiatorId, Integer activityId);
    List<SharingActivityHelpedEntity> getHelpedListComboByProposeId(Long intitiatorId, Integer activityId,Integer proposeId);
}
