package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantActivityDTO;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.entity.MerchantRoomParamsEntity;

import java.util.List;
import java.util.Map;

/**
 * 商户端包房参数管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
public interface MerchantRoomParamsService extends CrudService<MerchantRoomParamsEntity, MerchantRoomParamsDTO> {
    PageData<MerchantRoomParamsDTO> listPage(Map<String,Object> params);
    void remove(Long id,int status);
    //根据商户编号和参数内容查询
    List<MerchantRoomParamsEntity> getByMerchantIdAndContent(Long merchantId, String content,int status);
    //根据状态获取预约参数
    List<MerchantRoomParamsEntity> getAllByStatus(int status);
}