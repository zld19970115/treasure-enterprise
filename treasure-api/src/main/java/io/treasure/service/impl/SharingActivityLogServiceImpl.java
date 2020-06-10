package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.SharingActivityDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dao.SharingInitiatorDao;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingActivityLogEntity;
import io.treasure.entity.SharingInitiatorEntity;
import io.treasure.service.SharingActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SharingActivityLogServiceImpl {

    @Autowired(required = false)
    private SharingActivityLogDao sharingActivityLogDao;

    /**
     * 取得本活动参加助力的详情，指定用户
     * @param intitiatorId
     * @param activityId
     * @return
     */
    public List<SharingActivityLogEntity> getList(Long intitiatorId, Integer activityId){

            QueryWrapper<SharingActivityLogEntity> sieqw = new QueryWrapper<>();
            sieqw.eq("initiator_id",intitiatorId);
            sieqw.eq("activity_id",activityId);

            return sharingActivityLogDao.selectList(sieqw);
    }


    /**
     * 取得指定用户参加的次数
     * @param intitiatorId
     * @param activityId
     * @return
     */
    public Integer getCount(Long intitiatorId, Integer activityId){
        QueryWrapper<SharingActivityLogEntity> sieqw = new QueryWrapper<>();
        sieqw.eq("initiator_id",intitiatorId);
        sieqw.eq("activity_id",activityId);

        return sharingActivityLogDao.selectCount(sieqw);

    }


    /**
     * 检查用户助力的次数，非新用户才有效时可用
     * @param intitiatorId
     * @param activityId
     * @param helperMobile
     * @return
     */
    public Integer isHelpedCount(Long intitiatorId,Integer activityId,String helperMobile){

        QueryWrapper<SharingActivityLogEntity> sieqw = new QueryWrapper<>();
        sieqw.eq("initiator_id",intitiatorId);
        sieqw.eq("activity_id",activityId);
        sieqw.eq("helper_mobile",helperMobile);

        return sharingActivityLogDao.selectCount(sieqw);
    }


    /**
     * 插入新记录，向助力日志中
     * @param
     * @return
     */
    public boolean insertOne(SharingActivityLogEntity sharingActivityLogEntity){
        /*   预留
        if(isHelpedCount(sharingActivityLogEntity.getInitiatorId(),sharingActivityLogEntity.getActivityId(),sharingActivityLogEntity.getHelperMobile())==0)
        */
        sharingActivityLogDao.insert(sharingActivityLogEntity);
        return true;
    }




}
