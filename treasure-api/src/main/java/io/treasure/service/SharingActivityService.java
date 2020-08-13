package io.treasure.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.entity.SharingActivityEntity;

import java.util.Date;
import java.util.List;

public interface SharingActivityService {

    /**
     * 检查活动是否有效，不存在或过期均无效
     * @return
     */
    boolean isEnableActivityById(Integer said);

    /**
     * 根据活动取得活动详情
     * @param said
     * @param enableOnly
     * @return
     */
    SharingActivityEntity getOneById(Integer said, boolean enableOnly);

    /**
     * 取得订单位列表，根据活动有效性
     * @param enableOnly
     * @return
     */
    List<SharingActivityEntity> getList(boolean enableOnly);
    List<SharingActivityEntity> getListByMerchantIdAndStatus(long MerchantId,Date now);
    void insertOne(SharingActivityEntity sharingActivityEntity);
    void updateOne(SharingActivityEntity sharingActivityEntity);
}
