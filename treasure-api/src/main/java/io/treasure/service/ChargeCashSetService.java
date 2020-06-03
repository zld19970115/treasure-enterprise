package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.ChargeCashSetDTO;
import io.treasure.entity.ActivityEntity;
import io.treasure.entity.ChargeCashSetEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 现金充值设置表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-18
 */
public interface ChargeCashSetService extends CrudService<ChargeCashSetEntity, ChargeCashSetDTO> {

    ChargeCashSetEntity selectByCash(BigDecimal cash);

    PageData<ChargeCashSetDTO> cashSetPageList(Map<String, Object> params);

    Integer cashSetUpdate(ChargeCashSetDTO dto);

    Integer cashSetDel(Long id);

    Integer cashSetAdd(ChargeCashSetDTO dto);

    ChargeCashSetEntity cashSetById(Long id);

    List<ChargeCashSetDTO>  select();
}
