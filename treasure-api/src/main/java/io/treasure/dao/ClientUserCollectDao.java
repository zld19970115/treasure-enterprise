package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.ClientUserCollectEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户收藏
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-02
 */
@Mapper
public interface ClientUserCollectDao extends BaseDao<ClientUserCollectEntity> {
     Integer selectByUidAndMid(@Param("userId")Long userId,@Param("martId")Long martId);
}