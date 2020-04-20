package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.ConsumptionRankingDto;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.TopSellersRankingDto;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.vo.ConsumptionRankingVo;
import io.treasure.vo.TopSellersRankingVo;
import org.springframework.web.bind.annotation.RequestBody;

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
}
