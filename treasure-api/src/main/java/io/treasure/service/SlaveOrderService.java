package io.treasure.service;

import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.SlaveOrderEntity;

import io.treasure.common.service.CrudService;

import java.util.List;


/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface SlaveOrderService extends CrudService<SlaveOrderEntity, SlaveOrderDTO> {

    List<SlaveOrderEntity> selectByOrderId(String orderId);
}