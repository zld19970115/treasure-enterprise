package io.treasure.service;

import io.treasure.entity.MasterOrderEntity;

import java.util.List;

public interface OrderRewardWithdrawRecordService {

    boolean addRecord(MasterOrderEntity entity);

    boolean updateCopiedStatus(List<Long> ids);
}
