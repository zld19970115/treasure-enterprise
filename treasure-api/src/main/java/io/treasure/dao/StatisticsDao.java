package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.ConsumptionRankingDto;
import io.treasure.dto.DaysTogetherPageDTO;
import io.treasure.dto.MerchantAccountDto;
import io.treasure.dto.TopSellersRankingDto;
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
    int todayReserve( Map<String, Object> params);
    int todayQuit( Map<String, Object> params);
    double todayMoney(Map<String, Object> params);
    int monthOrder( Map<String, Object> params);
    int monthReserve(  Map<String, Object> params);
    int monthQuit(Map<String, Object> params);
    double monthMoney( Map<String, Object> params);
    double allMoney(Long merchantId);
    int assignOrder( Map<String, Object> params);
    int assignReserve(Map<String, Object> params);
    int assignQuit( Map<String, Object> params);
    double assignMoney( Map<String, Object> params);
    List<TopSellersRankingVo> getTopSellersRanking(TopSellersRankingDto dto);
    List<ConsumptionRankingVo> getConsumptionRanking(ConsumptionRankingDto dto);
    BigDecimal getTotalCash(Map<String, Object> params);
    List<MerchantAccountVo> getMerchantAccount(MerchantAccountDto dto);
    BigDecimal getCompletaOrder(Map<String, Object> params);
    List<ReturnDishesPageVo> getReturnDishesPage(Map<String,Object> map);
    List<VisualizationRoomVo> getVisualizationRoom(Map<String,Object> map);
    List<VisualizationRoomVo> selectRoomAllByMid(Map<String,Object> map);
    List<DaysTogetherPageDTO> daysTogetherPage(Map<String, Object> params);
    DaysTogetherStatisticsVo daysTogetherStat(Map<String, Object> params);
    List<StatSdayDetailPageVo> statSdayDetailPage(Map<String, Object> params);
    StatSdayDetailPageVo statSdayDetailPageTotalRow(@Param("ids") List<Long> ids);
    FmisHomeVo fmisHome(Map<String, Object> params);
    List<MerchantPageVo> merchantPage(Map<String, Object> params);
}
