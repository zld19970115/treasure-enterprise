package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.*;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsDao extends BaseDao<MasterOrderEntity> {
    int todayOrder(Map<String, Object> params);
    int todayReserve(Map<String, Object> params);
    int todayQuit(Map<String, Object> params);
    double todayMoney(Map<String, Object> params);
    int monthOrder(Map<String, Object> params);
    int monthReserve(Map<String, Object> params);
    int monthQuit(Map<String, Object> params);
    double monthMoney(Map<String, Object> params);
    double allMoney(Long merchantId);
    int assignOrder(Map<String, Object> params);
    int assignReserve(Map<String, Object> params);
    int assignQuit(Map<String, Object> params);
    double assignMoney(Map<String, Object> params);
    List<TopSellersRankingVo> getTopSellersRanking(TopSellersRankingDto dto);
    List<ConsumptionRankingVo> getConsumptionRanking(ConsumptionRankingDto dto);
    BigDecimal getTotalCash(Map<String, Object> params);
    List<MerchantAccountVo> getMerchantAccount(MerchantAccountDto dto);
    BigDecimal getCompletaOrder(Map<String, Object> params);
    List<ReturnDishesPageVo> getReturnDishesPage(Map<String, Object> map);
    List<VisualizationRoomVo> getVisualizationRoom(Map<String, Object> map);
    List<VisualizationRoomVo> selectRoomAllByMid(Map<String, Object> map);
    List<DaysTogetherPageDTO> daysTogetherPage(Map<String, Object> params);
    DaysTogetherStatisticsVo daysTogetherStat(Map<String, Object> params);
    List<StatSdayDetailPageVo> statSdayDetailPage(Map<String, Object> params);
    StatSdayDetailPageVo statSdayDetailPageTotalRow(Map<String, Object> params);
    MerchantAccountVo getMerchantAccountPageTotalRow(MerchantAccountDto dto);
    FmisHomeVo fmisHome(Map<String, Object> params);
    List<MerchantPageVo> merchantPage(Map<String, Object> params);
    List<MerchantPageVo> smsMerchantPage(Map<String, Object> params);
    List<EChartInfoVo> userChartByDay(Map<String, Object> params);
    List<EChartInfoVo> userChartByYear(Map<String, Object> params);
    List<EChartInfoVo> merchantChartByDay(Map<String, Object> params);
    List<EChartInfoVo> merchantChartByYear(Map<String, Object> params);
    List<EChartOrderInfoVo> orderChartByDay(Map<String, Object> params);
    List<EChartOrderInfoVo> orderChartByYear(Map<String, Object> params);
    RealTimeOrder realTimeOrder(Map<String, Object> params);
    PointsConfigDto pointsConfigInfo();
    void updatePointsConfig(Map<String, Object> params);
    DaysTogetherPageDTO daysTogetherPageTotalRow(Map<String, Object> params);
    List<MerPushUserVo> selectMerPushUser(Map<String, Object> params);
}
