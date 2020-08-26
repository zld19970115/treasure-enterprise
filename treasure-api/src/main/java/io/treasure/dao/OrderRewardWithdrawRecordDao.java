package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.ActivityGiveEntity;
import io.treasure.entity.ActivityGiveLogEntity;
import io.treasure.entity.OrderRewardWithdrawRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderRewardWithdrawRecordDao extends BaseDao<OrderRewardWithdrawRecord> {

}
