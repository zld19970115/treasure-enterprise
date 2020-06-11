package io.treasure.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.entity.SharingInitiatorEntity;

import java.util.List;

public interface SharingInitiatorService {

    /**
     * 检查活动是否有效，不存在或过期均无效,
     * @return
     */
    boolean isExistByInitiatorId(Long intitiatorId);

    /**
     * 检查是否已经参加过本编号的活动了
     * @param intitiatorId
     * @param saId
     * @return
     */
    boolean isParticipated(Long intitiatorId, Integer saId);


    /**
     * 根据活动取得活动详情,或者查询某人是否参加过某活动了
     * @param intitiatorId:client_id /saId：活动编号
     * @return
     */
    SharingInitiatorEntity getOneByInitiatorId(Long intitiatorId, Integer saId);

    List<SharingInitiatorEntity> getList(Long intitiatorId, Integer saId);
    /**
     * 取得订单位列表，根据活动有效性
     * @param result 1:只查已经成功的,0查询未成功的,null查询所有
     * @return
     */
    List<SharingInitiatorEntity> getList(Integer result);

    /**
     * 修改个人助力状态
     * @param sharingInitiatorEntity
     */
    boolean updateOneByInitiatorId(SharingInitiatorEntity sharingInitiatorEntity);

    /**
     * 发起活动时处理
     * @param sharingInitiatorEntity
     * @return
     */
    boolean insertOne(SharingInitiatorEntity sharingInitiatorEntity);

    Integer getCount(Long intitiatorId, Integer saId, Integer result);

}
