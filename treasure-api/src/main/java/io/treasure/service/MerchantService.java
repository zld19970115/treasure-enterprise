package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;

/**
 * 商户表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-23
 */
public interface MerchantService extends CrudService<MerchantEntity, MerchantDTO> {
    //删除
    void remove(long id);
}