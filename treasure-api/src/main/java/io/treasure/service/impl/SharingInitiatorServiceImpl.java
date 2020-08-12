package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.SharingActivityDao;
import io.treasure.dao.SharingActivityLogDao;
import io.treasure.dao.SharingInitiatorDao;
import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.SharingActivityEntity;
import io.treasure.entity.SharingInitiatorEntity;
import io.treasure.service.QRCodeService;
import io.treasure.service.SharingActivityService;
import io.treasure.service.SharingInitiatorService;
import io.treasure.utils.TimeUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SharingInitiatorServiceImpl implements SharingInitiatorService {

    @Autowired(required = false)
    private SharingInitiatorDao sharingInitiatorDao;
    @Autowired
    private SharingActivityService sharingActivityService;
    @Autowired
    private QRCodeService qrCodeService;

    @Override
    public String initQRCode(String client_id)throws Exception{

        String url ="https://jubaoapp.com:8443";
        Map<String,String> map = new HashMap<>();
        map.put("id",client_id);
        //map.put("saId","777");
        return qrCodeService.generateQrAndUrl(url,map);
    }
    /**
     * @param sharingInitiatorEntity    //发起活动
     * @return
     */
    @Override
    public SharingInitiatorEntity insertOne(SharingInitiatorEntity sharingInitiatorEntity) throws Exception {

        //检查助力状态
        SharingInitiatorEntity inProcessingObject = getOne(sharingInitiatorEntity.getInitiatorId(), sharingInitiatorEntity.getSaId(), true);
        if(inProcessingObject != null){
            return inProcessingObject;      //当前助力活动已经存在
        }else{
            //成功次数
            Integer successTimes = getCount(sharingInitiatorEntity.getInitiatorId(), sharingInitiatorEntity.getSaId(), ESharingInitiator.COMPLETE_SUCCESS.getCode());
            //允许成功次数
            Integer allowSuccessTimes = 0;
            SharingActivityEntity sharingActivityEntity = sharingActivityService.getOneById(sharingInitiatorEntity.getSaId(), true);
            if(sharingActivityEntity != null)
                allowSuccessTimes = sharingActivityEntity.getProposeTimes();

            //无进行中的助力活动，且参加次数未超限
            if(successTimes<allowSuccessTimes){
                sharingInitiatorEntity.setQrCode(initQRCode(sharingInitiatorEntity.getInitiatorId()+""));
                sharingInitiatorDao.insert(sharingInitiatorEntity);     //插入新记录(活动编号)
                return sharingInitiatorEntity;
            }else{
                return null;//参加活动次数超限
            }
        }
    }

    /**
     * @param intitiatorId  client_id 用户id
     * @return  返回该用户当前是否有进行中的活动
     */
    @Override
    public boolean unfinishedCheck(Long intitiatorId,Integer saId){

        SharingInitiatorEntity siEntity = getOne(intitiatorId,saId,true);

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
    public SharingInitiatorEntity getOne(Long intitiatorId,Integer saId,boolean onlyEnable){

        //优先选择进行中的，如果没有进行中的选择成功了的,即1优先，2在后面
        QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();
        sieqw.eq("initiator_id",intitiatorId);
        if(saId != null)
            sieqw.eq("sa_id",saId);

            sieqw.eq("status",1);
        SharingInitiatorEntity sharingInitiatorEntity = sharingInitiatorDao.selectOne(sieqw);
        if(onlyEnable)
            return sharingInitiatorEntity;

        if(sharingInitiatorEntity != null)
            return sharingInitiatorEntity;
        return sharingInitiatorDao.getLastOne(intitiatorId,saId);
    }

    @Override
    public SharingInitiatorEntity getCurrentOne(Long intitiatorId, Integer saId){

        QueryWrapper<SharingInitiatorEntity> sieqw = new QueryWrapper<>();
        sieqw.eq("initiator_id",intitiatorId);
        if(saId != null)
            sieqw.eq("sa_id",saId);
        return sharingInitiatorDao.getLastOne(intitiatorId,saId);
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
            SharingInitiatorEntity completeEntity = sharingInitiatorDao.getLastOne(intitiatorId,saId);
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

            SharingInitiatorEntity inprocessItem = getOne(initiatorId,saId,false);
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

    @Override
    public void setReadedStatus(Long intitiatorId,Integer saId){
        sharingInitiatorDao.setReadedStatus(intitiatorId,saId);
    }



}
