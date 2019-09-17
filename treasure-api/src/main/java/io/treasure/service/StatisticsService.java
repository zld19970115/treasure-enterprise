package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.entity.MasterOrderEntity;
import org.apache.ibatis.annotations.Param;

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
    int assignOrder(String startTime1,String endTime1,Long merchantId);
    int assignReserve(String startTime1,String endTime1,Long merchantId);
    int assignQuit(String startTime1,String endTime1,Long merchantId);
    double assignMoney(String startTime1,String endTime1,Long merchantId);
}
