package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.SharingActivityDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dao.SharingInitiatorDao;
import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingInitiatorEntity;
import io.treasure.service.SharingActivityService;
import io.treasure.service.SharingInitiatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SharingInitiatorServiceImpl implements SharingInitiatorService {

    @Autowired(required = false)
    private SharingInitiatorDao sharingInitiatorDao;
    @Autowired
    private SharingActivityService sharingActivityService;

    /**
     * @param sharingInitiatorEntity    //发起活动
     * @return
     */
    @Override
    public boolean insertOne(SharingInitiatorEntity sharingInitiatorEntity){

        //检查助力状态
        SharingInitiatorEntity inProcessingObject = getOne(sharingInitiatorEntity.getInitiatorId(), sharingInitiatorEntity.getSaId(), ESharingInitiator.IN_PROCESSING.getCode());
        if(inProcessingObject != null)
            return true;
        //成功次数
        Integer successTimes = getCount(sharingInitiatorEntity.getInitiatorId(), sharingInitiatorEntity.getSaId(), ESharingInitiator.COMPLETE_SUCCESS.getCode());

        //允许成功次数
        Integer allowSuccessTimes = 0;
        SharingActivityEntity sharingActivityEntity = sharingActivityService.getOneById(sharingInitiatorEntity.getSaId(), true);
        if(sharingActivityEntity != null)
            allowSuccessTimes = sharingActivityEntity.getProposeTimes();
        //无进行中的助力活动，且参加次数未超限
        if(inProcessingObject == null){
            if(successTimes<allowSuccessTimes){

                sharingInitiatorDao.insert(sharingInitiatorEntity);     //插入新记录(活动编号)
                return true;

            }else{
                return false;//参加活动次数超限
            }
        }
        return true;//正在活动中无须二次插入新活动
    }

    /**
     * @param intitiatorId  client_id 用户id
     * @return  返回该用户当前是否有进行中的活动
     */
    @Override
    public boolean unfinishedCheck(Long intitiatorId,Integer saId){

        SharingInitiatorEntity siEntity = getOne(intitiatorId,saId,ESharingInitiator.IN_PROCESSING.getCode());

        if(siEntity == null)
            return false;
        return true;

    }

    /**
     * @param intitiatorId id
     * @param saId 活动id
     * @return  返回用户参加活动的次数
     */
    @Override
    public int getCount(Long intitiatorId, Integer saId,Integer status){
        Integer count = sharingInitiatorDao.getCount(intitiatorId,saId,status);
        if(count == null)
            return 0;
        return count;
    }

    @Override
    public void closeActivity(Long intitiatorId,Integer saId){
        sharingInitiatorDao.closeActivity(intitiatorId,saId);
    }

    @Override
    public SharingInitiatorEntity getOne(Long intitiatorId,Integer saId,Integer... status){

        QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();

        sieqw.eq("initiator_id",intitiatorId);
        if(saId != null)
            sieqw.eq("sa_id",saId);
        if(status != null)
            sieqw.in("status",status);

        return sharingInitiatorDao.selectOne(sieqw);
    }

    @Override
    public SharingInitiatorEntity getLastInProcessOne(Long intitiatorId,Integer saId){

        QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();

        sieqw.eq("initiator_id",intitiatorId);
        if(saId != null)
            sieqw.eq("sa_id",saId);
        sieqw.eq("status",ESharingInitiator.IN_PROCESSING.getCode());

        SharingInitiatorEntity inProcessEntity = sharingInitiatorDao.selectOne(sieqw);
        if(inProcessEntity != null){
            return inProcessEntity;
        }else{
            //没有进行中的活动，使用完成的活动
            QueryWrapper<SharingInitiatorEntity> sieqw1 = new QueryWrapper<>();

            sieqw1.eq("initiator_id",intitiatorId);
            if(saId != null)
                sieqw1.eq("sa_id",saId);
            sieqw1.eq("status",ESharingInitiator.COMPLETE_SUCCESS.getCode());
            sieqw1.orderByDesc("start_time");
            SharingInitiatorEntity completeEntity = sharingInitiatorDao.selectOne(sieqw1);
            return completeEntity;
        }
    }

    /**
     *
     * @param intitiatorId 用户id
     * @param saId  助力编号
     * @param status 结果
     * @return
     */
    @Override
    public List<SharingInitiatorEntity> getList(Long intitiatorId,Integer saId,Integer... status){

        QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();

        sieqw.eq("initiator_id",intitiatorId);
        if(saId != null)
            sieqw.eq("sa_id",saId);
        if(status != null)
            sieqw.in("status",status);

        return sharingInitiatorDao.selectList(sieqw);
    }


    /**
     * 修改个人助力状态 修改为成功或失败
     * @param sharingInitiatorEntity
     */
    @Override
    public boolean updateById(SharingInitiatorEntity sharingInitiatorEntity){
        Integer proposeId = sharingInitiatorEntity.getProposeId();

        if(proposeId == null){
            Long initiatorId = sharingInitiatorEntity.getInitiatorId();
            Integer saId = sharingInitiatorEntity.getSaId();

            if(saId == null || initiatorId == null)
                return false;//更新失败

            SharingInitiatorEntity inprocessItem = getOne(initiatorId,saId,ESharingInitiator.IN_PROCESSING.getCode());
            if(inprocessItem != null){
                sharingInitiatorEntity.setProposeId(inprocessItem.getProposeId());
            }else{
                System.out.println("错误：未取得活动编号等信息");
                return false;//活动为空
            }
        }

        sharingInitiatorDao.updateById(sharingInitiatorEntity);
        return true;

    }




}
