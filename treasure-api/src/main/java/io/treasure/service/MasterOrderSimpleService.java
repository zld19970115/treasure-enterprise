package io.treasure.service;

import io.treasure.common.utils.Result;
import io.treasure.entity.OrderSimpleEntity;

import java.util.List;

public interface MasterOrderSimpleService {

    List<OrderSimpleEntity> getOrderList(Long merchantId, Integer index, Integer pageNumber);
    Integer getCount(Long merchantId);
}
