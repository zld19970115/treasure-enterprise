package io.treasure.dao;

import io.treasure.vo.DistributionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClearOrderDao{

    void clearSlaveOrders(@Param("startTime")String startTime, @Param("stopTime")String stopTime);
    void clearRooms(@Param("startTime")String startTime, @Param("stopTime")String stopTime);
    List<DistributionVo> distributionList(@Param("startTime")String startTime, @Param("stopTime")String stopTime);
    void clearMasterOrders(@Param("startTime")String startTime, @Param("stopTime")String stopTime);

}
