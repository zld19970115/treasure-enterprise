package io.treasure.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ScheduleMasterOrderMapper {

    //!从单清台   注意必须行清此单 然后再执行主单清台，否则调用的主单标志位将不存在了  -->
    public int clearSlaveOrder(@Param("startTime") String startTime, @Param("stopTime") String stopTime);

    //!主单清台   注意必须行清此单 然后再执行主单清台，否则调用的主单标志位将不存在了  -->
    public int clearMasterOrder(@Param("startTime") String startTime, @Param("stopTime") String stopTime);

    //释放房间   注意必须行清此单 然后再执行主单清台，否则调用的主单标志位将不存在了  -->
    public int clearMerchantRoom(@Param("startTime") String startTime, @Param("stopTime") String stopTime);

      //ctSlaveOrderMapper.updateFinished(specifyDateTime[0],specifyDateTime[1]);
        //ctMerchantRoomParamsSetMapper.updateFreeRoom(specifyDateTime[0],specifyDateTime[1]);
        //ctMasterOrderMapper.updateFinished(specifyDateTime[0],specifyDateTime[1]);
}