package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.MerchantOrderDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.vo.BackDishesVo;
import io.treasure.vo.GoodPrinterVo;
import io.treasure.vo.OrderVo;
import io.treasure.vo.RoomOrderPrinterVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Mapper
public interface OrderDao{


    Integer selectNewOrderCount(Long merchantId);
    Integer selectNewOrderCountOnly(Long merchantId);
    Integer selectActtachItemCount(Long merchantId);
    Integer selectAttachRoomCount(Long merchantId);
    Integer selectRefundOrderCount(Long merchantId);
    Integer selectDetachItemCount(Long merchantId);
    Integer selectInProcessCount(Long merchantId);


}
