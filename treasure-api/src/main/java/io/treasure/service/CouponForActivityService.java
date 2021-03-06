package io.treasure.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.page.PageData;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.entity.SignedRewardSpecifyTimeEntity;
import java.math.BigDecimal;
import java.text.ParseException;

import java.util.Map;

public interface CouponForActivityService {

    BigDecimal getClientActivityCoinsVolume(Long clientUser_id);
    BigDecimal getClientCanUseTotalCoinsVolume(Long clientUser_id);
    boolean coinsIsEnable(Long clientUser_id,BigDecimal coins);
    void updateCoinsConsumeRecord(Long clientUser_id,BigDecimal coins,String orderId);
    boolean updateActivityCoinsConsumeRecord(Long clientUser_id,BigDecimal coins,String orderId);
    void resumeActivityCoinsRecord(Long clientUser_id,BigDecimal coins,String order_id);
    void resumeAllCoinsRecord(Long clientUser_id,String orderId);

    Boolean clientCheckForSignedForReward(Long clientUser_id) throws ParseException;
    void insertClientActivityRecord(Long clientId,BigDecimal bd,Integer method,Integer validity, ESharingRewardGoods.ActityValidityUnit actityValidityUnit) throws ParseException;

    Map<String,String> getSignedActivityCoinsNumberInfo() throws ParseException;
    IPage<MulitCouponBoundleEntity> getRecordByClientId(Long clientUser_id,boolean onlyEnable,Integer page,Integer num);
    SignedRewardSpecifyTimeEntity getParamsById(Long id);

    PageData pageList(Map<String, Object> map);

    Boolean checkClientDrawTimes(Long clientId) throws ParseException;

    BigDecimal getCanUseCurrentActivityRewardAmount(Long clientId);
    BigDecimal getClientSharingActivityCoinsVolume(Long clientUser_id);


    boolean canResume(String orderId);
    /**
     * 清除对象的订单标记
     * @param orderId
     */
    void clearProcessingFlag(String orderId);
    /**
     * 检查是否允许消费宝币，同一个单号在活动中仅能使用一次,如果记录中已经扣除过则不能进行再扣除
     * @return
     */
    boolean canConsume(String orderId);
}
