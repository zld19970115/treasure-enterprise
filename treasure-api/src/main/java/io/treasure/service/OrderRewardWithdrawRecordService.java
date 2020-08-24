package io.treasure.service;

import io.treasure.enm.EOrderRewardWithdrawRecord;
import io.treasure.entity.MasterOrderEntity;

import java.util.List;

public interface OrderRewardWithdrawRecordService {

    boolean addPreWithdrawRecord(MasterOrderEntity entity);

    boolean updateWithdrawFlagById(List<Long> ids, EOrderRewardWithdrawRecord eStatus);
}
