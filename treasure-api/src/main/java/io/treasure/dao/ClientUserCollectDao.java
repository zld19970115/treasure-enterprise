package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.ClientUserCollectDTO;
import io.treasure.entity.ClientUserCollectEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-02
 */
@Mapper
public interface ClientUserCollectDao extends BaseDao<ClientUserCollectEntity> {
    ClientUserCollectDTO selectByUidAndMid(@Param("userId") Long userId, @Param("martId") Long martId);
    void changeStatus(@Param("userId") Long userId, @Param("martId") Long martId);
    List<ClientUserCollectDTO> getCollectMerchantByUserId(Map params);
}