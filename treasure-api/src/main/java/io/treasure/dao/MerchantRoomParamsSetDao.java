package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.MerchantRoomParamsSetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商户端包房设置管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
@Mapper
public interface MerchantRoomParamsSetDao extends BaseDao<MerchantRoomParamsSetEntity> {
    //修改状态
    void updateStatus(long id, int status);

    //查询指定日期、时间段内可用包房
    List<MerchantRoomParamsSetDTO> getAvailableRoomsByData(@Param("useDate")Date useDate, @Param("roomParamsId")long roomParamsId,@Param("merchantId")long merchantId);
}