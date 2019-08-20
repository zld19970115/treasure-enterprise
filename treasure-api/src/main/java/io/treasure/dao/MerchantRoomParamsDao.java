package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantRoomParamsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商户端包房参数管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@Mapper
public interface MerchantRoomParamsDao extends BaseDao<MerchantRoomParamsEntity> {
    void updateStatusById(Long id,int status);
    List<MerchantRoomParamsEntity> getByMerchantIdAndContent(Long merchantId,String content,int status);
    List<MerchantRoomParamsEntity> getAllByStatus(int status);
}