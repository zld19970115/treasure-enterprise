package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MasterOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StatisticsDao extends BaseDao<MasterOrderEntity> {
    int todayOrder(@Param("format") String format, @Param("merchantId") Long merchantId);
    int todayReserve(@Param("format") String format, @Param("merchantId") Long merchantId);
    int todayQuit(@Param("format") String format, @Param("merchantId") Long merchantId);
    double todayMoney(@Param("format") String format, @Param("merchantId") Long merchantId);
    int monthOrder(@Param("month") String month, @Param("merchantId") Long merchantId);
    int monthReserve(@Param("month") String month, @Param("merchantId") Long merchantId);
    int monthQuit(@Param("month") String month, @Param("merchantId") Long merchantId);
    double monthMoney(@Param("month") String month, @Param("merchantId") Long merchantId);
    double allMoney(Long merchantId);

}
