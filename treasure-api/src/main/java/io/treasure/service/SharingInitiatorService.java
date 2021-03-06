package io.treasure.service;

import io.treasure.enm.ESharingInitiator;
import io.treasure.entity.SharingInitiatorEntity;

import java.util.List;

public interface SharingInitiatorService {


    String initQRCode(String client_id,Integer saId)throws Exception;
    SharingInitiatorEntity insertOne(SharingInitiatorEntity sharingInitiatorEntity) throws Exception;

    SharingInitiatorEntity getCurrentOne(Long intitiatorId, Integer saId);

    /**
     * @param intitiatorId  client_id 用户id
     * @return  返回该用户当前是否有进行中的活动
     */
    boolean unfinishedCheck(Long intitiatorId,Integer saId);

    /**
     * @param intitiatorId id
     * @param saId 活动id
     * @return  返回用户参加活动的次数
     */
    int getCount(Long intitiatorId, Integer saId,Integer status);

    SharingInitiatorEntity getOne(Long intitiatorId,Integer saId,boolean onlyEnable);
    SharingInitiatorEntity getLastInProcessOne(Long intitiatorId,Integer saId);

    void closeActivity(Long intitiatorId,Integer saId);


    /**
     *
     * @param intitiatorId 用户id
     * @param saId  助力编号
     * @param status 结果
     * @return
     */
    List<SharingInitiatorEntity> getList(Long intitiatorId,Integer saId,Integer... status);


    /**
     * 修改个人助力状态 修改为成功或失败
     * @param sharingInitiatorEntity
     */
    boolean updateById(SharingInitiatorEntity sharingInitiatorEntity);

    void setReadedStatus(Long intitiatorId,Integer saId);


}
