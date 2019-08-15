package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.entity.MerchantRoomEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
    //根据名称和商户id查询
    List getByNameAndMerchantId(String name,long merchantId,int type);
    //根据商户编号查询包房信息
    List getByMerchantId(long merchantId,int status);
}