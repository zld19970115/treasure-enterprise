package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantAdvertExtendDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.entity.MerchantAdvertExtendEntity;

import java.util.Map;

/**
 * 商户广告位推广
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
public interface MerchantAdvertExtendService extends CrudService<MerchantAdvertExtendEntity, MerchantAdvertExtendDTO> {
    PageData<MerchantAdvertExtendDTO> listPage(Map<String,Object> params);
}