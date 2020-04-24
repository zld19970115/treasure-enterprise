package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.common.utils.Result;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.entity.MerchantRoomParamsSetEntity;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商户端包房设置管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
public interface MerchantRoomParamsSetService extends CrudService<MerchantRoomParamsSetEntity, MerchantRoomParamsSetDTO> {
    //删除
    void remove(long id,int status);
    //修改状态
    void updateStatus(long id,int status);
    Result setRoom(long merchantId,long creater);
    //查询指定日期、时间段内可用包房
    List getAvailableRoomsByData(Date useDate, long roomParamsId,Integer type,long merchantId) throws ParseException;

    int getAvailableRooms(long bigTime,long merchantId);

    int getAvailableRoomsDesk(long bigTime,long merchantId);

    MerchantRoomParamsSetEntity selectByMartIdAndRoomIdAndRoomId(Long merchantId, Long roomId, long roomSetId, String format);
}