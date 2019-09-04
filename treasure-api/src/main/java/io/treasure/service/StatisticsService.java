package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.entity.MasterOrderEntity;

public interface StatisticsService extends CrudService<MasterOrderEntity, MasterOrderDTO> {

    int todayOrder(String format, Long merchantId);
    int todayReserve(String format, Long merchantId);
    int todayQuit(String format, Long merchantId);
    double todayMoney(String format, Long merchantId);
    int monthOrder(String month, Long merchantId);
     int monthReserve(String month, Long merchantId);
    int monthQuit(String month, Long merchantId);
    double monthMoney(String month, Long merchantId);
    double allMoney(Long merchantId);
}
