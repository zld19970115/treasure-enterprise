package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantActivityDTO;
import io.treasure.dto.MerchantAdvertExtendDTO;
import io.treasure.entity.MerchantActivityEntity;

import java.util.Map;

/**
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
public interface MerchantActivityService extends CrudService<MerchantActivityEntity, MerchantActivityDTO> {
    //删除
    void remove(Long id,int status);
    PageData<MerchantActivityDTO> listPage(Map<String,Object> params);
}