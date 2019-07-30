package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantRoomEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
@Mapper
public interface MerchantRoomDao extends BaseDao<MerchantRoomEntity> {
    //修改状态
    void updateStatusById(long id,int status);
}