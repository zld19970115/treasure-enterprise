package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.CrudService;
import io.treasure.dto.ClientUserCollectDTO;
import io.treasure.entity.ClientUserCollectEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-02
 */
public interface ClientUserCollectService extends CrudService<ClientUserCollectEntity, ClientUserCollectDTO> {
    ClientUserCollectDTO selectByUidAndMid(@Param("userId")Long userId, @Param("martId")Long martId);
    void changeStatus(@Param("userId")Long userId, @Param("martId")Long martId);
    PageData<ClientUserCollectDTO> getCollectMerchantByUserId(Map<String, Object> params);
}