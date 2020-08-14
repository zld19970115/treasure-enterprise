package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.SharingActivityHelpedLogDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingActivityHelpedEntity;
import io.treasure.entity.SharingActivityLogEntity;
import io.treasure.entity.SharingInitiatorEntity;
import io.treasure.service.SharingActivityLogService;
import io.treasure.service.SharingActivityService;
import io.treasure.service.SharingInitiatorService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Service
public class SharingActivityLogServiceImpl implements SharingActivityLogService {

    @Autowired(required = false)
    private SharingActivityLogDao sharingActivityLogDao;
    @Autowired
    private SharingActivityService sharingActivityService;
    @Autowired
    private SharingInitiatorService sharingInitiatorService;
    @Autowired(required = false)
    private SharingActivityHelpedLogDao sharingActivityHelpedLogDao;

    /**
     * 取得本活动参加助力的详情，指定用户
     * @param intitiatorId
     * @param activityId
     * @return
     */
    @Override
    public List<SharingActivityLogEntity> getList(long intitiatorId, Integer activityId,Integer proposeSequeueNo){

            QueryWrapper<SharingActivityLogEntity> sieqw = new QueryWrapper<>();
            sieqw.eq("initiator_id",intitiatorId);
            if(activityId != null)
                sieqw.eq("activity_id",activityId);
            if(proposeSequeueNo != null)
                sieqw.eq("propose_sequeue_no",proposeSequeueNo);

            return sharingActivityLogDao.selectList(sieqw);
    }


    /**
     * 取得指定用户参加的次数,及助力的次数
     * @param intitiatorId
     * @param activityId
     * @return
     */
    @Override
    public Integer getCount(long intitiatorId, Integer activityId,Integer proposeSequeueNo){

        Integer helpCount = sharingActivityLogDao.getHelpCount(intitiatorId, activityId, proposeSequeueNo);

        if(helpCount == null)
            return 0;
        return helpCount;
    }

    @Override
    public int getHelpedCount(String mobile){
        int res = 0;
        Integer helpCountxx = sharingActivityLogDao.getHelpCountxx(mobile);
        if(helpCountxx != null)
            res = helpCountxx;
        return res;
    }

    @Override
    public int getHelpedCountForCurrentSharing(long intitiatorId,int activityId,int proposeSequeueNo,String mobile){
        Integer helpCountCurrentSharing = sharingActivityLogDao.getHelpCountCurrentSharing(intitiatorId, activityId, proposeSequeueNo, mobile);
        if(helpCountCurrentSharing == null)
            return 0;
        return helpCountCurrentSharing;
    }

    @Override
    public int getHelpedCount(String mobile,int activityId, Date start, Date stop){
        int res = 0;

        QueryWrapper<SharingActivityLogEntity> sae = new QueryWrapper<>();
        sae.between("create_pmt",start,stop);
        sae.eq("helper_mobile",mobile);
        sae.eq("activity_id",activityId);

        Integer helpCountxx = sharingActivityLogDao.selectCount(sae);

        if(helpCountxx != null)
            res = helpCountxx;
        return res;
    }

    /**
     * 插入新记录，向助力日志中
     * @param
     * @return
     */
    @Override
    public boolean insertOne(@NotEmpty SharingActivityLogEntity sharingActivityLogEntity){
        Integer allowHelpersNum = 0;
        Integer completeCount = 0;
        SharingActivityEntity sharingActivityEntity = sharingActivityService.getOneById(sharingActivityLogEntity.getActivityId(), true);

        if(sharingActivityEntity != null){
            allowHelpersNum = sharingActivityEntity.getPersonLimit();
        }

        SharingInitiatorEntity sharingInitiatorEntity = sharingInitiatorService.getOne(
                sharingActivityLogEntity.getInitiatorId(), sharingActivityLogEntity.getActivityId(), false);

        if(sharingInitiatorEntity != null){
            completeCount = getCount(sharingActivityLogEntity.getInitiatorId(), sharingActivityLogEntity.getActivityId(),sharingInitiatorEntity.getProposeId());
            if(completeCount < allowHelpersNum){

                sharingActivityLogEntity.setCreatePmt(new Date());
                
                sharingActivityLogDao.insert(sharingActivityLogEntity);
                System.out.println("助力完成001");
                return true;//助力完成
            }else{
                System.out.println("错误：助力已结束");
                return false;//错误：助力已结束
            }
        }else{
            System.out.println("错误：助力活动不存在");
            return false;//错误：活动不存在
        }
    }

    @Override
    public Integer getRewardSum(long intitiatorId, int activityId,int proposeSequeueNo){

        Integer res = sharingActivityLogDao.getSum(intitiatorId,activityId,proposeSequeueNo);
        if(res == null)
            return 0;
        return res;
    }

    @Override
    public SharingActivityLogEntity getOne(Long initiatorId,Integer activityId,Integer proposeSequeueNo,String helperMobile){

        QueryWrapper<SharingActivityLogEntity> sale = new QueryWrapper<>();

        if (activityId != null)             sale.eq("activity_id",activityId);
        if(proposeSequeueNo != null)        sale.eq("propose_sequeue_no",proposeSequeueNo);
        if(helperMobile != null)            sale.eq("helper_mobile",helperMobile);
        if(initiatorId != null)             sale.eq("initiator_id",initiatorId);

        return sharingActivityLogDao.selectOne(sale);
    }

    @Override
    public List<SharingActivityLogEntity> getHelpedList(long intitiatorId, Integer activityId,Integer proposeSequeueNo){

        QueryWrapper<SharingActivityLogEntity> sieqw = new QueryWrapper<>();
        sieqw.eq("initiator_id",intitiatorId);
        if(activityId != null)
            sieqw.eq("activity_id",activityId);
        if(proposeSequeueNo != null)
            sieqw.eq("propose_sequeue_no",proposeSequeueNo);

        return sharingActivityLogDao.selectList(sieqw);
    }

    @Override
    public List<SharingActivityHelpedEntity> getHelpedListCombo(Long intitiatorId, Integer activityId){

        return sharingActivityHelpedLogDao.selectHelpedListCombo(intitiatorId, activityId);
    }
    @Override
    public List<SharingActivityHelpedEntity> getHelpedListComboByProposeId(Long intitiatorId, Integer activityId,Integer proposeId,Integer helpersNum){
        return sharingActivityHelpedLogDao.selectHelpedListComboByProposeIds(intitiatorId, activityId,proposeId,helpersNum);
    }
    @Override
    public List<SharingActivityHelpedEntity> getHelpedListComboUnread(Long intitiatorId, Integer activityId,Integer helpersNum){

        return sharingActivityHelpedLogDao.selectHelpedListComboUnread(intitiatorId, activityId,helpersNum);
    }

    @Override
    public List<SharingInitiatorEntity> getUnreadedLogList(long intitiatorId,int activityId){
        return sharingActivityLogDao.getUnreadedList(intitiatorId,activityId);
    }
}
