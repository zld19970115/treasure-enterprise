package io.treasure.service;

import io.treasure.common.utils.Result;

public interface MasterOrderSimpleService {

    Result getOrderList(Long merchantId,Long merchantIdPosition,Integer status,Integer checkStatus,Integer index,Integer pageNumber);
}
