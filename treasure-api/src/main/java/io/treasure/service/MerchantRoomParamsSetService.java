package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.MerchantRoomParamsSetEntity;

/**
 * 商户端包房设置管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
public interface MerchantRoomParamsSetService extends CrudService<MerchantRoomParamsSetEntity, MerchantRoomParamsSetDTO> {
    //删除
    void remove(long id,int status);
}