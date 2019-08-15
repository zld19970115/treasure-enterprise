package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.entity.MerchantRoomEntity;

import java.util.List;

/**
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
public interface MerchantRoomService extends CrudService<MerchantRoomEntity, MerchantRoomDTO> {
    //删除
    void remove(long id,int status);
    //根据名称和商户id查询
    List getByNameAndMerchantId(String name, long merchantId,int type);
    //根据商户编号查询包房信息
    List getByMerchantId(long merchantId,int status);
}