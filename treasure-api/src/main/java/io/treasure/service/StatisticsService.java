package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.entity.MasterOrderEntity;
import org.apache.ibatis.annotations.Param;

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
}
