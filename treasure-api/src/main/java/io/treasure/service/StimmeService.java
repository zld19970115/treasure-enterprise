package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.StimmeDTO;
import io.treasure.entity.StimmeEntity;

import java.util.List;
import java.util.Map;

public interface StimmeService  extends CrudService<StimmeEntity, StimmeDTO> {
    List<StimmeEntity> selectBymerchantId(Map<String, Object> params);
    StimmeDTO selectByOrderId(String orderId);
}
