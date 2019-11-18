package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.MerchantRoomEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
@Mapper
public interface MerchantRoomDao extends BaseDao<MerchantRoomEntity> {
    //修改状态
    void updateStatusById(@Param("id") long id,@Param("status") int status);
    //根据名称和商户id查询
    List getByNameAndMerchantId(@Param("name") String name,@Param("merchantId") long merchantId, @Param("type")int type);
    //根据商户编号查询包房信息
    List getByMerchantId(@Param("merchantId") long merchantId,@Param("status") int status);
    List<MerchantRoomDTO> listPage(Map<String,Object> params);
    List<MerchantRoomParamsSetDTO> selectRoomAlreadyPage(Map<String, Object> params);
    List<MerchantRoomParamsSetDTO>  selectRoomDate(Map<String,Object> params);
    List<MerchantRoomParamsSetDTO> selectByDateAndMartId(Map<String,Object> params);
    List<MerchantRoomParamsSetDTO> selectByDateAndMartId2(Map<String,Object> params);
    MerchantRoomEntity getmerchantroom(long merchantId);
    List<MerchantRoomParamsSetDTO>  selectRoomByTime(Map<String, Object> params);
}