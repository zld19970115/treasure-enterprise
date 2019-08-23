package io.treasure.service;

import io.treasure.dto.MasterOrderDTO;
import io.treasure.enm.Order;
import io.treasure.entity.MasterOrderEntity;

import io.treasure.common.service.CrudService;

import java.util.Date;


/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface MasterOrderService extends CrudService<MasterOrderEntity, MasterOrderDTO> {
    void updateStatusAndReason(long id, int status, long verify, Date verify_date, String verify_reason);
}