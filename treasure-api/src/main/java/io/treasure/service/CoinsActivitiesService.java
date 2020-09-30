package io.treasure.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.utils.Result;
import io.treasure.enm.ECoinsActivities;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.CoinsActivitiesEntity;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.CoinsActivityVo;
import io.treasure.vo.PrizeUserInfoVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static io.treasure.enm.ECoinsActivities.ExpireTimeUnit.UNIT_MONTHS;
import static io.treasure.enm.ECoinsActivities.NewUserWinAwards.FOCRE_NEW_USER_WIN_AWARDS;
import static io.treasure.enm.ECoinsActivities.TimeMode.EVERY_DAY_OPENING;
import static java.time.LocalDate.now;

public interface CoinsActivitiesService {

    void updateVisualJackpot() throws ParseException;
    CoinsActivitiesEntity getCoinsActivityById(Long id,boolean defaultParams);
    CoinsActivitiesEntity getDefaultParams();
    CoinsActivitiesEntity initatorEntityViaDefault(CoinsActivitiesEntity coinsActivitiesEntity);
    Date generateExpireTimeForActivities(CoinsActivitiesEntity coinsActivitiesEntity) throws ParseException;
//======================================以上基本活动处理=======================================================
    int isOnTime(CoinsActivitiesEntity coinsActivitiesEntity) throws ParseException;
    BigDecimal getSumForCoinsActivityToday(CoinsActivitiesEntity coinsActivity) throws ParseException;
    BigDecimal jackpotRemaining(CoinsActivitiesEntity coinsActivity) throws ParseException;
    int getRecordsNumToday(CoinsActivitiesEntity coinsActivity) throws ParseException;
    boolean shouldBeFirstPrize(CoinsActivitiesEntity coinsActivity,Long clientId) throws ParseException;
    //boolean checkFirstPrizeInSegmentSubStatus(CoinsActivitiesEntity coinsActivity, Integer maxmum, int records, Long clientId) throws ParseException;
    //boolean checkSegmentSubStatus(CoinsActivitiesEntity coinsActivity, boolean isHeaderSegment, int pos, int sumRecords, Long clientId) throws ParseException;
//==========================================用户相关====================================================

    boolean firstDrawCoinsActivities(Long clientId) throws ParseException;
    boolean checkTimesTimeRange(CoinsActivitiesEntity coinsActivity,Long clientId) throws ParseException;
    Boolean checkTimesEveryDay(CoinsActivitiesEntity coinsActivity,Long clientId) throws ParseException;
    BigDecimal getCurrentActivityEnableCoinsSumByClientId(Long clientUser_id,Long  coinsActivityId);
    Double insertCoinsActivityRecordByClientId(CoinsActivitiesEntity coinsActivity,Long clientId,BigDecimal award,Integer method) throws ParseException;
    //==========================================层级调用--业务===========================================================
    Result clientDraw(Long clientId) throws ParseException;
    Result getCoinsActivityInfo() throws ParseException;
    Result getCountDownInfo() throws ParseException;
    Result coinActivityResult(int code, String msg, Object data);

    Result getFirstPrizeList() throws ParseException;
    Result coinActivityResultWithPrizeUser(int code,String msg,List<PrizeUserInfoVo> frizeDateInfo);
    Result coinActivityResultWithCoinsActivity(int code, String msg, CoinsActivityVo coinsActivityVo,boolean isOver);


}
