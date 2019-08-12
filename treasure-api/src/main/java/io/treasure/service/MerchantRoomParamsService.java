package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.entity.MerchantRoomParamsEntity;

import java.util.List;

/**
 * 商户端包房参数管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
public interface MerchantRoomParamsService extends CrudService<MerchantRoomParamsEntity, MerchantRoomParamsDTO> {
    void remove(Long id,int status);
    //根据商户编号和参数内容查询
    List<MerchantRoomParamsEntity> getByMerchantIdAndContent(Long merchantId, String content,int status);
}