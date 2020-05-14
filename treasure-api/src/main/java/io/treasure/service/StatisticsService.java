package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.*;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.vo.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface StatisticsService extends CrudService<MasterOrderEntity, MasterOrderDTO> {

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
    List<ConsumptionRankingVo> getConsumptionRanking(@RequestBody ConsumptionRankingDto dto);
    BigDecimal getTotalCash(Map<String, Object> params);
    PageData<MerchantAccountVo> getMerchantAccount(MerchantAccountDto dto);
    BigDecimal getCompletaOrder(Map<String, Object> params);
    PageData<ReturnDishesPageVo> getReturnDishesPage(Map<String,Object> map);
    List<VisualizationRoomVo> getVisualizationRoom(Map<String,Object> map);
    PageData<DaysTogetherPageDTO> daysTogetherPage(Map<String, Object> params);
    DaysTogetherStatisticsVo daysTogetherStat(Map<String, Object> params);
    PageTotalRowData<StatSdayDetailPageVo> statSdayDetailPage(Map<String, Object> params);
    FmisHomeVo fmisHome(Map<String, Object> params);
    PageData<MerchantPageVo> merchantPage(Map<String, Object> params);
    EChartVo userChart(Map<String, Object> params);
}
